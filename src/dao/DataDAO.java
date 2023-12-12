package dao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

import entities.CombinedData;
import entities.Emissions;
import entities.User;

public class DataDAO {

	protected static EntityManagerFactory emf = 
			Persistence.createEntityManagerFactory("mydb");

	public DataDAO() {
		// TODO Auto-generated constructor stub
	}


	public List<CombinedData> readAllFromFile() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		List<CombinedData> emissions = new ArrayList<CombinedData>();
		emissions = em.createQuery("from CombinedData", CombinedData.class).getResultList();
		em.getTransaction().commit();
		em.close();
		return emissions;
	}
	
	public List<CombinedData> searchCategory(String category) {
	    EntityManager em = emf.createEntityManager();
	    em.getTransaction().begin();
	    List<CombinedData> results = em.createQuery("SELECT e FROM CombinedData e WHERE e.category = :category", CombinedData.class)
	            .setParameter("category", category)
	            .getResultList();
	    em.getTransaction().commit();
	    em.close();
	    return results;
	}
	
	public void persist(CombinedData d) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(d);
		em.getTransaction().commit();
		em.close();
	}
	
	public CombinedData merge(CombinedData d) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		CombinedData updatedData = em.merge(d);
		em.getTransaction().commit();
		em.close();
		return updatedData;
	}
	
	public String update(int id, CombinedData d) {
	    EntityManager em = emf.createEntityManager();
	    em.getTransaction().begin();
	    CombinedData existingData = em.find(CombinedData.class, id);

	    if (existingData != null) {
	        existingData.setCategory(d.getCategory());
	        existingData.setDescription(d.getDescription());
	        existingData.setGasUnits(d.getGasUnits());
	        existingData.setPredicted2023(d.getPredicted2023());
	        existingData.setReadings2023(d.getReadings2023());
	        
	        em.getTransaction().commit();
	        em.close();
	        return "Data Updated";
	    } else {
	        em.getTransaction().rollback();
	        em.close();
	        return "Data with ID " + id + " not found";
	    }
	}

	public String removeData(String category, String gasUnits) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		try {
			CombinedData data = em.createQuery("SELECT u FROM CombinedData u WHERE u.category = :category AND u.gasUnits = :gasUnits", CombinedData.class)
					.setParameter("category", category)
					.setParameter("gasUnits", gasUnits)
					.getSingleResult();

			if  (data != null) {
				em.remove(data);
				em.getTransaction().commit();
				em.close();
				return "Data Removed";
			} else {
				em.getTransaction().rollback();
				em.close();
				return "Data Not Found";
			}
		} catch (NoResultException e) {
			em.getTransaction().rollback();
			em.close();
			return "Data Not Found";
		}
	}

}
