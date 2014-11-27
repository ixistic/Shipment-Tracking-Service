package ku.shipment.server.service;

import ku.shipment.server.service.jpa.JpaDaoFactory;


public abstract class DaoFactory {
	
private static DaoFactory factory;
	
	/** 
	 * this class shouldn't be instantiated, but constructor must be visible to subclasses. 
	 */
	protected DaoFactory() {
		
	}
	
	/**
	 * Get a singleton instance of the DaoFactory.
	 * @return instance of a concrete DaoFactory
	 */
	public static DaoFactory getInstance() {
		if (factory == null) {
			String factoryclass = System.getProperty("sts.daofactory");
			if(factoryclass != null){
				ClassLoader loader = DaoFactory.class.getClassLoader();
				try {
					factory = (DaoFactory)loader.loadClass(factoryclass).newInstance();
				} catch (InstantiationException | IllegalAccessException
						| ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			if(factory == null){
				setFactory( JpaDaoFactory.getInstance() );
			}
		}
		return factory;
	}
	
	public static void setFactory(DaoFactory daoFactory){
		factory = daoFactory;
	}
	
	public abstract ShipmentDao getShipmentDao();
	
	public abstract void shutdown();
}
