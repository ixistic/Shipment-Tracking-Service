package ku.shipment.server.service;

import ku.shipment.server.service.jpa.JpaShipmentDaoFactory;

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

	public static void setFactory(ShipmentDaoFactory daoFactory) {
		factory = daoFactory;
	}

	public abstract ShipmentDao getShipmentDao();

	public abstract void shutdown();
}
