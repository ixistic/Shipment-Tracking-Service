package ku.shipment.server.service.jpa;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import jersey.repackaged.com.google.common.collect.Lists;
import ku.shipment.server.entity.User;
import ku.shipment.server.service.UserDao;

public class JpaUserDao implements UserDao {
	private final EntityManager em;

	public JpaUserDao(EntityManager em) {
		this.em = em;
		// createTestShipment();
	}

	private void createTestShipment() {
		long id = 101; // usually we should let JPA set the id
		if (find(id) == null) {
			User test = new User();
			test.setId(id);
			save(test);
		}
		id++;
		if (find(id) == null) {
			User test2 = new User();
			test2.setId(id);
			save(test2);
		}
	}

	@Override
	public User find(long id) {
		return em.find(User.class, id);
	}

	@Override
	public List<User> findAll() {
		Query query = em.createQuery("SELECT c FROM User c");
		List<User> users = Lists.newArrayList(query.getResultList());
		return Collections.unmodifiableList(users);
	}

	@Override
	public User findByAccessToken(String accessToken) {
		Query query = em
				.createQuery("SELECT c FROM User c WHERE c.accessToken = :accessToken");
		query.setParameter("accessToken", accessToken);
		List<User> list = query.getResultList();
		if (list.size() == 1)
			return list.get(0);
		return null;
	}

	@Override
	public User findByEmail(String email) {
		Query query = em
				.createQuery("SELECT c FROM User c WHERE c.email = :email");
		query.setParameter("email", email);
		List<User> list = query.getResultList();
		if (list.size() == 1)
			return list.get(0);
		return null;
	}

	@Override
	public boolean delete(long id) {
		User user = find(id);
		EntityTransaction tx = em.getTransaction();
		if (user == null)
			return false;
		try {
			em.getTransaction().begin();
			em.remove(user);
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
	public boolean save(User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't save a null user");
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist(user);
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
	public boolean update(User update) {
		if (update == null)
			throw new IllegalArgumentException("Can't update a null user");
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			User user = find(update.getId());
			if (user == null)
				throw new IllegalArgumentException("Can't update a null user");
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
