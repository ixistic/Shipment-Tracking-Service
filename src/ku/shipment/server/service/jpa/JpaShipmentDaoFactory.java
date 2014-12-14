package ku.shipment.server.service.jpa;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import ku.shipment.server.service.ShipmentDaoFactory;
import ku.shipment.server.service.ShipmentDao;

/**
 * JpaDaoFactory is a factory for DAO that use the Java Persistence API (JPA) to
 * persist objects. The factory depends on the configuration information in
 * META-INF/persistence.xml.
 * 
 * @author Veerapat Threeravipark 5510547022
 * 
 */
public class JpaShipmentDaoFactory extends ShipmentDaoFactory {
	private static final String PERSISTENCE_UNIT = "shipments";
	private static JpaShipmentDaoFactory factory;
	private ShipmentDao shipmentDao;
	private final EntityManagerFactory emf;
	private EntityManager em;
	private static Logger logger;

	static {
		logger = Logger.getLogger(JpaShipmentDaoFactory.class.getName());
	}

	/**
	 * Constructor of this class.
	 */
	public JpaShipmentDaoFactory() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		em = emf.createEntityManager();
		shipmentDao = new JpaShipmentDao(em);
	}

	/**
	 * Get the instance of DaoFactory.
	 * 
	 * @return instance of DaoFactory.
	 */
	public static JpaShipmentDaoFactory getInstance() {
		if (factory == null) {
			factory = new JpaShipmentDaoFactory();
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
	public ShipmentDao getShipmentDao() {
		return shipmentDao;
	}
}
