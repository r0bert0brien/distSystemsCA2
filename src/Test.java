public class Test {
	
	public Test() {

	UserDAO userdao = new UserDAO();
	User user = new User("RobertO","test");
	userdao.persist(user);
	}
	
	public static void main(String[] args) {
		new Test();
	}
}
