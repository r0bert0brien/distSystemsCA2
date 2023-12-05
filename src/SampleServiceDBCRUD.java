import java.io.FileReader;
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

@Path("/sampleserviceDBCRUD")
public class SampleServiceDBCRUD {
	
	private static Map<String, User> users = new HashMap<String, User>();
	private UserDAO dao = new UserDAO();

	@GET
    @Path("/hello")
    @Produces("text/plain")
    public String hello(){
        return "Hello World";    
    }
	
	@GET
    @Path("/helloworld")
    @Produces("text/plain")
    public String helloWorld(){
        return "Hello World New";    
    }
	
	@GET
    @Path("/echo/{message}")
    @Produces("text/plain")
    public String echo(@PathParam("message")String message){
        return message;  
    }
	
	@GET
    @Path("/newEcho/{message}")
    @Produces("text/plain")
    public String newEcho(@PathParam("message")String message){
        return message;  
    }
	
	@POST
	@Path("/createuserxml")
    @Consumes("application/xml")
    public String addEmployee(User user){
		dao.persist(user);
		return "Employee added " +user.getUserName();		
    }
	
	@POST
	@Path("/createuserjson")
    @Consumes("application/json")
    public String addJSONEmployee(User user){
		return "Employee added " +user.getUserName();		
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
}

