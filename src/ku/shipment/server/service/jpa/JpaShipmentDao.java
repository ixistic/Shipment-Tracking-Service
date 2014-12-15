package ku.shipment.server.service.jpa;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import jersey.repackaged.com.google.common.collect.Lists;
import ku.shipment.server.entity.Item;
import ku.shipment.server.entity.Shipment;
import ku.shipment.server.entity.User;
import ku.shipment.server.service.ShipmentDao;

/**
 * Data access object for saving and retrieving shipment, using JPA
 * 
 * @author veerapat
 * 
 */
public class JpaShipmentDao implements ShipmentDao {
	private final EntityManager em;

	/**
	 * Constructor of this class, initial entity manager
	 * 
	 * @param em
	 *            entity manager
	 */
	public JpaShipmentDao(EntityManager em) {
		this.em = em;
		// createTestShipment();
	}

	private void createTestShipment() {
		long id = 101; // usually we should let JPA set the id
		if (find(id) == null) {
			Shipment test = new Shipment();
			test.setId(id);
			save(test);
		}
		id++;
		if (find(id) == null) {
			Shipment test2 = new Shipment();
			test2.setId(id);
			save(test2);
		}
	}

	@Override
	public Shipment find(long id) {
		// return em.find(Shipment.class, id);
		Query query = em
				.createQuery("SELECT c FROM Shipment c WHERE c.id = :id");
		query.setParameter("id", id);
		List<Shipment> list = query.getResultList();
		if (list.size() == 1)
			return list.get(0);
		return null;
	}

	@Override
	public List<Shipment> findAll() {
		Query query = em.createQuery("SELECT c FROM Shipment c");
		List<Shipment> shipments = Lists.newArrayList(query.getResultList());
		return Collections.unmodifiableList(shipments);
	}

	@Override
	public Shipment findByUserAndId(User user, long id) {
		Query query = em
				.createQuery("SELECT c FROM Shipment c WHERE c.user = :user AND c.id = :id");
		query.setParameter("user", user);
		query.setParameter("id", id);
		List<Shipment> list = query.getResultList();
		if (list.size() == 1)
			return list.get(0);
		return null;
	}

	@Override
	public boolean delete(long id) {

		Shipment shipment = find(id);
		if (shipment == null)
			return false;
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		for (Item item : shipment.getItem()) {
			Query query1 = em
					.createQuery("DELETE FROM Item c WHERE c.id = :id");
			query1.setParameter("id", item.getId());
			query1.executeUpdate();
		}
		Query query2 = em
				.createQuery("DELETE FROM Shipment c WHERE c.id = :id");
		query2.setParameter("id", id);
		query2.executeUpdate();
		tx.commit();
		return true;
	}

	@Override
	public boolean save(Shipment shipment) {
		if (shipment == null)
			throw new IllegalArgumentException("Can't save a null shipment");
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist(shipment);
			tx.commit();
			return true;
		} catch (EntityExistsException ex) {
			Logger.getLogger(this.getClass().getName())
					.warning(ex.getMessage());
			if (tx.isActive())
				try {
					tx.rollback();
				} catch (Exception e) {
				}
			return false;
		}
	}

	@Override
	public boolean update(Shipment update) {
		if (update == null)
			throw new IllegalArgumentException("Can't update a null shipment");
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Shipment shipment = find(update.getId());
			if (shipment == null)
				throw new IllegalArgumentException(
						"Can't update a null shipment");
			em.merge(update);
			tx.commit();
			return true;
		} catch (EntityExistsException ex) {
			Logger.getLogger(this.getClass().getName())
					.warning(ex.getMessage());
			if (tx.isActive())
				try {
					tx.rollback();
				} catch (Exception e) {
				}
			return false;
		}
	}

}
