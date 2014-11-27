package ku.shipment.server.service;

import java.util.List;

import ku.shipment.server.entity.Shipment;

public interface ShipmentDao {

	public abstract Shipment find(long id);

	public abstract List<Shipment> findAll();
	
	public abstract List<Shipment> findByTitle(String prefix);
	
	public abstract boolean delete(long id);
	
	public abstract boolean save(Shipment contact);
	
	public abstract boolean update(Shipment update);
	

}
