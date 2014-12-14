package ku.shipment.server.service;

import ku.shipment.server.service.jpa.JpaShipmentDaoFactory;

/**
 * DaoFactory defines methods for obtaining instance of data access objects.
 * 
 * @author Veerapat Threeravipark 5510547022
 * 
 */
public abstract class ShipmentDaoFactory {

	private static ShipmentDaoFactory factory;

	/**
	 * this class shouldn't be instantiated, but constructor must be visible to
	 * subclasses.
	 */
	protected ShipmentDaoFactory() {

	}

	/**
	 * Get a singleton instance of the DaoFactory.
	 * 
	 * @return instance of a concrete DaoFactory
	 */
	public static ShipmentDaoFactory getInstance() {

		if (factory == null) {
			setFactory(JpaShipmentDaoFactory.getInstance());
		}

		return factory;
	}

	/**
	 * Set Factory
	 * 
	 * @param daoFactory
	 *            user dao factory
	 */
	public static void setFactory(ShipmentDaoFactory daoFactory) {
		factory = daoFactory;
	}

	/**
	 * Get an instance of a data access object for shipment objects. Subclasses
	 * of the base DaoFactory class must provide a concrete instance of this
	 * method that returns a ShipmentDao suitable for their persistence
	 * framework.
	 * 
	 * @return instance of Shipment's DAO
	 */
	public abstract ShipmentDao getShipmentDao();

	/**
	 * Shutdown all persistence services.
	 * This method gives the persistence framework a chance to
	 * gracefully save data and close databases before the
	 * application terminates.
	 */
	public abstract void shutdown();
}
