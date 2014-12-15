package ku.shipment.server.service;

import ku.shipment.server.service.jpa.JpaUserDaoFactory;

/**
 * DaoFactory defines methods for obtaining instance of data access objects.
 * 
 * @author veerapat
 * 
 */
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

	/**
	 * Set Factory
	 * 
	 * @param daoFactory
	 *            user dao factory
	 */
	public static void setFactory(UserDaoFactory daoFactory) {
		factory = daoFactory;
	}

	/**
	 * Get an instance of a data access object for user objects. Subclasses of
	 * the base DaoFactory class must provide a concrete instance of this method
	 * that returns a UserDao suitable for their persistence framework.
	 * 
	 * @return instance of User's DAO
	 */
	public abstract UserDao getUserDao();

	/**
	 * Shutdown all persistence services.
	 * This method gives the persistence framework a chance to
	 * gracefully save data and close databases before the
	 * application terminates.
	 */
	public abstract void shutdown();
}
