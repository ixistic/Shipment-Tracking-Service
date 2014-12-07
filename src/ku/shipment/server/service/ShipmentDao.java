package ku.shipment.server.service;

import java.util.List;

import ku.shipment.server.entity.Shipment;
import ku.shipment.server.entity.User;

public interface ShipmentDao {

	public abstract Shipment find(long id);

	public abstract List<Shipment> findAll();
	
	public abstract Shipment findByUserAndId(User user, long id);
	
	public abstract boolean delete(long id);
	
	public abstract boolean save(Shipment shipment);
	
	public abstract boolean update(Shipment update);

}
