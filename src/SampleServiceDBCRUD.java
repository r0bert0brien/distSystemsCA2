import java.io.FileReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import entities.CombinedData;
import entities.Description;
import entities.Emissions;
import entities.User;
import entities.XMLParse;
import dao.DataDAO;
import dao.UserDAO;

@Path("/sampleserviceDBCRUD")
public class SampleServiceDBCRUD {

	private static Map<String, User> users = new HashMap<String, User>();
	private UserDAO dao = new UserDAO();
	private DataDAO emdao = new DataDAO();

	
	//Combined Data Resources
	@GET
	@Path("/all")
	@Produces("application/xml")
	public List<CombinedData> getAllEmissions() {
		return emdao.readAllFromFile();
	}
	
	@POST
	@Path("/createdata")
	@Consumes("application/xml")
	public String addData(CombinedData d){
		emdao.persist(d);
		return "Data added " +d.getCategory() + "\n" + d.getGasUnits();		
	}
	
	@GET
	@Path("/category/{category}")
	@Produces("application/xml")
	public List<CombinedData> searchByCategory(@PathParam("category")String category){
	    return emdao.searchCategory(category);    
	}
	
	@PUT
    @Path("/updatedata/{id}")
    @Produces("application/xml")
    public String updateuser(@PathParam("id")int id,CombinedData d){
		return emdao.update(id,d);	
    }
	
	@DELETE
	@Path("/deletedata/{category}/{gasUnits}")
	@Produces("application/xml")
	public String removeData(
	        @PathParam("category") String category,
	        @PathParam("gasUnits") String gasUnits) {
		return emdao.removeData(category, gasUnits);
	}

	
	//User Resources
	@POST
	@Path("/createuserxml")
	@Consumes("application/xml")
	public String addEmployee(User user){
		dao.persist(user);
		return "User added " +user.getUserName();		
	}

	@POST
	@Path("/createuserjson")
	@Consumes("application/json")
	public String addJSONEmployee(User user){
		return "User added " +user.getUserName();		
	}
	
	@POST
	@Path("/login/{username}/{password}")
	@Produces("text/plain")
	public String login(
	        @PathParam("username") String userName,
	        @PathParam("password") String password) {
	    UserDAO userDao = new UserDAO();
	    return userDao.login(userName, password);
	}
	
	@GET
	@Path("/usersxmlfromdb")
	@Produces("application/xml")
	public List<User> getEmployeesFromDB(){
		return dao.getAllUsers();
	}

	@GET
	@Path("/usersjsonfromdb")
	@Produces("application/json")
	public List<User> getEmployeesFromDBJSON(){
		return dao.getAllUsers();
	}

	@GET
	@Path("/userfromDBXML/{userName}")
	@Produces("application/xml")
	public User getEmployeeByNameFromDBXML(@PathParam("userName")String userName){
		return dao.getUserByName(userName);	
	}
	
	@DELETE
	@Path("/userfromDBXML/{id}")
	@Produces("application/xml")
	public String deleteUser(@PathParam("id")int id){
		return dao.removeuser(id);	
	}
	
	@PUT
    @Path("/updateuser/{id}")
    @Produces("application/xml")
    public String updateuser(@PathParam("id")int id,User user){
		return dao.update(id,user);	
    }
}

