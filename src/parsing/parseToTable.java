package parsing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import entities.CombinedData;
import entities.Description;
import entities.Emissions;
import entities.XMLParse;

public class parseToTable {

	public static void main(String[] args) {
		List<XMLParse> xmlsFromXML = parseXMLData();
		List<Description> descriptions = parseDescriptions();
		List<Emissions> emissionsFromJSON = parseJSONData();
		persistCombinedData(xmlsFromXML, emissionsFromJSON, descriptions);
	}
	
	private static String formatValue(String value) {
	    double numericValue = Double.parseDouble(value);
	    DecimalFormat decimalFormat = new DecimalFormat("#.#############");
	    return decimalFormat.format(numericValue);
	}
	
	private static List<Description> parseDescriptions() {
	    List<Description> descriptions = new ArrayList<>();
	    try {
	        URL url = new URL("https://www.ipcc-nggip.iges.or.jp/EFDB/find_ef.php");
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");

	        InputStream inputStream = connection.getInputStream();
	        StringBuilder content = new StringBuilder();
	        int data;
	        while ((data = inputStream.read()) != -1) {
	            content.append((char) data);
	        }
	        inputStream.close();

	        String[] lines = content.toString().split("ipccTree.add");

	        for (String line : lines) {
	            if (line.contains("'")) {
	                String[] parts = line.split(",");
	                if (parts.length >= 4) {
	                    String description = parts[2].trim().replaceAll("[^0-9A-Za-z. ]", "");
	                    Description category = new Description(description);
	                    descriptions.add(category);
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return descriptions;
	}

	private static List<XMLParse> parseXMLData() {
		List<XMLParse> xmls = new ArrayList<>();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			URL url = new URL("https://cdr.eionet.europa.eu/ie/eu/mmr/art04-13-14_lcds_pams_projections/projections/envvxoklg/MMR_IRArticle23T1_IE_2016v2.xml");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			InputStream inputStream = connection.getInputStream();
			Document document = builder.parse(inputStream);

			NodeList rowList = document.getElementsByTagName("Row");

			for (int i = 0; i < rowList.getLength(); i++) {
				Node rowNode = rowList.item(i);

				if (rowNode.getNodeType() == Node.ELEMENT_NODE) {
					Element rowElement = (Element) rowNode;

					String year = getElementValue(rowElement, "Year");
					String scenario = getElementValue(rowElement, "Scenario");
					String value = getElementValue(rowElement, "Value");

					if ("2023".equals(year) && "WEM".equals(scenario) && isValidValue(value)) {
						value = formatValue(value);
						String category = getElementValue(rowElement, "Category__1_3");
						String gasUnits = getElementValue(rowElement, "Gas___Units");
						String nk = getElementValue(rowElement, "NK");

						XMLParse xmlparse = new XMLParse(category, year, scenario, gasUnits, nk, value);
						xmls.add(xmlparse);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmls;
	}

	private static boolean isValidValue(String value) {
		try {
			double val = Double.parseDouble(value);
			return val > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static String getElementValue(Element element, String tagName) {
		NodeList nodeList = element.getElementsByTagName(tagName).item(0).getChildNodes();
		if (nodeList.getLength() > 0) {
			Node node = nodeList.item(0);
			if (node != null) {
				return node.getNodeValue();
			}
		}
		return "";
	}

	private static List<Emissions> parseJSONData() {
	    List<Emissions> emissions = new ArrayList<>();

	    try {
	        File jsonFile = new File("GreenhouseGasEmissions2023.json");
	        ObjectMapper objectMapper = new ObjectMapper();

	        JsonNode rootNode = objectMapper.readTree(jsonFile);
	        JsonNode emissionsNode = rootNode.get("Emissions");

	        for (JsonNode emissionNode : emissionsNode) {
	            String category = emissionNode.get("Category").asText();
	            String gasUnits = emissionNode.get("Gas Units").asText();
	            double value = emissionNode.get("Value").asDouble();
	            String formattedValue = formatValue(String.valueOf(value));

	            Emissions e = new Emissions(category, gasUnits, formattedValue);
	            emissions.add(e);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return emissions;
	}
	
	private static void persistCombinedData(List<XMLParse> xmls, List<Emissions> emissions, List<Description> descriptions) {
	    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("mydb");
	    EntityManager entityManager = entityManagerFactory.createEntityManager();
	    EntityTransaction transaction = null;

	    try {
	        transaction = entityManager.getTransaction();
	        transaction.begin();

	        // Process XML data
	        for (XMLParse xml : xmls) {
	            boolean matched = false;

	            // Check for matching data in emissions (JSON)
	            for (Emissions emission : emissions) {
	                if (xml.getCategory().equals(emission.getCategory()) && xml.getGasUnits().equals(emission.getGasUnits())) {
	                    String variance = calculateVariance(xml.getValue(), String.valueOf(emission.getValue()));

	                   CombinedData combinedEntity = new CombinedData(
	                        xml.getCategory(),
	                        null,
	                        xml.getGasUnits(),
	                        xml.getValue(),
	                        String.valueOf(emission.getValue()),
	                        variance
	                    );
	                   
	                   for (Description d : descriptions) {
	                	    if (d.getDescription().contains(xml.getCategory())) {
	                	        combinedEntity.setDescription(d.getDescription());
	                	        break;
	                	    }
	                	}
	                    entityManager.persist(combinedEntity);
	                    matched = true;
	                    break;
	                }
	            }

	            // If no match found, add the XML data
	            if (!matched) {
	                CombinedData combinedEntity = new CombinedData(
	                    xml.getCategory(),
	                   null,
	                    xml.getGasUnits(),
	                    xml.getValue(),
	                    null,
	                    null
	                );
	                
	                for (Description d : descriptions) {
                	    if (d.getDescription().contains(xml.getCategory())) {
                	        combinedEntity.setDescription(d.getDescription());
                	        break;
                	    }
                	}
	                entityManager.persist(combinedEntity);
	            }
	        }
	        
	        for (Emissions emission : emissions) {
	            boolean matched = false;

	            // Check if the emissions data has a matching entry in XML data
	            for (XMLParse xml : xmls) {
	                if (emission.getCategory().equals(xml.getCategory()) && emission.getGasUnits().equals(xml.getGasUnits())) {
	                	matched = true;
	                    break;
	                }
	            }

	            // If no match found, add the JSON data
	            if (!matched) {
	                CombinedData combinedEntity = new CombinedData(
	                    emission.getCategory(),
	                    null,
	                    emission.getGasUnits(),
	                    null,
	                    String.valueOf(emission.getValue()),
	                    null
	                );
	                
	                for (Description d : descriptions) {
                	    if (d.getDescription().contains(emission.getCategory())) {
                	        combinedEntity.setDescription(d.getDescription());
                	        break;
                	    }
                	}
	                entityManager.persist(combinedEntity);
	            }
	        }

	        transaction.commit();
	    } catch (Exception e) {
	        if (transaction != null && transaction.isActive()) {
	            transaction.rollback();
	        }
	        e.printStackTrace();
	    } finally {
	        entityManager.close();
	        entityManagerFactory.close();
	    }
	}


	private static String calculateVariance(String predicted, String readings) {
	    Double predictedVal = Double.parseDouble(predicted);
	    double readingsVal = Double.parseDouble(readings);

	    double variance = readingsVal - predictedVal;
	    DecimalFormat decimalFormat = new DecimalFormat("#.#############");
	    String formattedVariance = decimalFormat.format(variance);

	    return formattedVariance;
	}

}
