package dao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

import entities.User;
	public class UserDAO {
		
		protected static EntityManagerFactory emf = 
		Persistence.createEntityManagerFactory("mydb");

		public UserDAO() {
			// TODO Auto-generated constructor stub
		}

		public void persist(User user) {
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			em.persist(user);
			em.getTransaction().commit();
			em.close();
		}
		
		public String login(String userName, String password) {
	        EntityManager em = emf.createEntityManager();
	        em.getTransaction().begin();
	        User user = null;
	        try {
	            user = em.createQuery("SELECT u FROM User u WHERE u.userName = :userName", User.class)
	                    .setParameter("userName", userName)
	                    .getSingleResult();
	        } catch (NoResultException e) {
	            // Handle case where the user doesn't exist
	            em.getTransaction().rollback();
	            em.close();
	            return "User not found";
	        }

	        if (user != null && user.getPassword().equals(password)) {
	            em.getTransaction().commit();
	            em.close();
	            return "Login successful";
	        } else {
	            em.getTransaction().rollback();
	            em.close();
	            return "Incorrect password";
	        }
	    }

		
		public String removeuser(int id) {
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			User e = em.createQuery("SELECT p FROM User p WHERE p.id = :id", User.class)
	                .setParameter("id", id)
	                .getSingleResult();
			em.remove(em.merge(e));
			em.getTransaction().commit();
			em.close();
			return "User Deleted";
		}
		
		public User merge(User user) {
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			User updatedUser = em.merge(user);
			em.getTransaction().commit();
			em.close();
			return updatedUser;
		}
		
		public String update(int id, User updatedUser) {
		    EntityManager em = emf.createEntityManager();
		    em.getTransaction().begin();
		    User existingUser = em.find(User.class, id);

		    if (existingUser != null) {
		        existingUser.setUserName(updatedUser.getUserName());
		        existingUser.setPassword(updatedUser.getPassword());

		        em.getTransaction().commit();
		        em.close();
		        return "User Updated";
		    } else {
		        em.getTransaction().rollback();
		        em.close();
		        return "User with ID " + id + " not found";
		    }
		}

		
		public List<User> getAllUsers() {
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			List<User> employees = new ArrayList<User>();
			employees = em.createQuery("from User").getResultList();
			em.getTransaction().commit();
			em.close();
			return employees;
		}

		public User getUserByName(String userName) {
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			User e = em.createQuery("SELECT p FROM User p WHERE p.userName = :userName", User.class)
	                .setParameter("userName", userName)
	                .getSingleResult();
			em.getTransaction().commit();
			em.close();
			return e;
		}
		


	}
