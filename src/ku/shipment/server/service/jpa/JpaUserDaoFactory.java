package ku.shipment.server.service.jpa;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import ku.shipment.server.service.UserDao;
import ku.shipment.server.service.UserDaoFactory;

/**
 * JpaDaoFactory is a factory for DAO that use the Java Persistence API (JPA) to
 * persist objects. The factory depends on the configuration information in
 * META-INF/persistence.xml.
 * 
 * @author veerapat
 * 
 */
public class JpaUserDaoFactory extends UserDaoFactory {
	private static final String PERSISTENCE_UNIT = "shipments";
	private static JpaUserDaoFactory factory;
	private UserDao userDao;
	private final EntityManagerFactory emf;
	private EntityManager em;
	private static Logger logger;

	static {
		logger = Logger.getLogger(JpaUserDaoFactory.class.getName());
	}

	/**
	 * Constructor of this class.
	 */
	public JpaUserDaoFactory() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		em = emf.createEntityManager();
		userDao = new JpaUserDao(em);
	}

	/**
	 * Get the instance of DaoFactory.
	 * 
	 * @return instance of DaoFactory.
	 */
	public static JpaUserDaoFactory getInstance() {
		if (factory == null) {
			factory = new JpaUserDaoFactory();
		}
		return factory;
	}

	@Override
	public void shutdown() {
		try {
			if (em != null && em.isOpen())
				em.close();
			if (emf != null && emf.isOpen())
				emf.close();
		} catch (IllegalStateException ex) {
			// SEVERE - highest
			logger.log(Level.SEVERE, ex.toString());
		}
	}

	@Override
	public UserDao getUserDao() {
		return userDao;
	}
}
