package ku.shipment.server.service;

import ku.shipment.server.service.jpa.JpaUserDaoFactory;

public abstract class UserDaoFactory {

	private static UserDaoFactory factory;

	/**
	 * this class shouldn't be instantiated, but constructor must be visible to
	 * subclasses.
	 */
	protected UserDaoFactory() {

	}

	/**
	 * Get a singleton instance of the DaoFactory.
	 * 
	 * @return instance of a concrete DaoFactory
	 */
	public static UserDaoFactory getInstance() {

		if (factory == null) {
			setFactory(JpaUserDaoFactory.getInstance());
		}

		return factory;
	}

	public static void setFactory(UserDaoFactory daoFactory) {
		factory = daoFactory;
	}

	public abstract UserDao getUserDao();

	public abstract void shutdown();
}

