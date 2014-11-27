package ku.shipment.server.service.jpa;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import jersey.repackaged.com.google.common.collect.Lists;
import ku.shipment.server.entity.Shipment;
import ku.shipment.server.service.ShipmentDao;

public class JpaShipmentDao implements ShipmentDao {
	private final EntityManager em;

	public JpaShipmentDao(EntityManager em) {
		this.em = em;
		createTestShipment();
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
		return em.find(Shipment.class, id);
	}

	@Override
	public List<Shipment> findAll() {
		Query query = em.createQuery("SELECT c FROM Shipment c");
		List<Shipment> shipments = Lists.newArrayList(query.getResultList());
		return Collections.unmodifiableList(shipments);
	}

	@Override
	public List<Shipment> findByTitle(String titlestr) {
		// LIKE does string match using patterns.
		Query query = em
				.createQuery("select c from Shipment c where LOWER(c.title) LIKE :title");
		// % is wildcard that matches anything
		query.setParameter("title", "%" + titlestr.toLowerCase() + "%");
		// now why bother to copy one list to another list?
		java.util.List<Shipment> result = Lists.newArrayList(query
				.getResultList());
		return result;
	}

	@Override
	public boolean delete(long id) {
		Shipment shipment = find(id);
		EntityTransaction tx = em.getTransaction();
		if (shipment == null)
			return false;
		try {
			em.getTransaction().begin();
			em.remove(shipment);
			em.getTransaction().commit();
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
