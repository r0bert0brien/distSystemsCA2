package entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement(name= "CombinedData")
public class CombinedData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String category;
	private String description;
	private String gasUnits;
	private String predicted2023;
	private String readings2023;
	private String variance;
	
	public CombinedData(String category, String description, String gasUnits, String predicted2023, String readings2023, String variance) {
		this.category = category;
		this.description = description;
		this.gasUnits = gasUnits;
		this.predicted2023 = predicted2023;
		this.readings2023 = readings2023;
		this.variance = variance;
	}
	
	public CombinedData() {
		
	}
	
	@XmlElement
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@XmlElement
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	@XmlElement
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement
	public String getGasUnits() {
		return gasUnits;
	}
	public void setGasUnits(String gasUnits) {
		this.gasUnits = gasUnits;
	}
	@XmlElement
	public String getPredicted2023() {
		return predicted2023;
	}
	public void setPredicted2023(String predicted2023) {
		this.predicted2023 = predicted2023;
	}
	@XmlElement
	public String getReadings2023() {
		return readings2023;
	}
	public void setReadings2023(String readings2023) {
		this.readings2023 = readings2023;
	}
	@XmlElement
	public String getVariance() {
		return variance;
	}
	public void setVariance(String variance) {
		this.variance = variance;
	}
	
	
}
