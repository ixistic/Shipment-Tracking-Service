package ku.shipment.server.service;

import java.util.List;

import ku.shipment.server.entity.Shipment;
import ku.shipment.server.entity.User;

/**
 * Interface defines the operations required by a DAO for shipment.
 * 
 * @author Veerapat Threeravipark 5510547022
 * 
 */
public interface ShipmentDao {

	/**
	 * Find a shipment by ID in shipments.
	 * 
	 * @param the
	 *            id of shipment to find
	 * @return the matching shipment or null if the id is not found
	 */
	public abstract Shipment find(long id);

	/**
	 * Return all the persisted shipments as a List. There is no guarantee what
	 * implementation of List is returned, so caller should use only List
	 * methods (not, say ArrayList).
	 * 
	 * @return list of all shipments in persistent storage. If no shipments,
	 *         returns an empty list.
	 */
	public abstract List<Shipment> findAll();

	/**
	 * Find shipment by user and id.
	 * 
	 * @param user
	 *            user object
	 * @param id
	 *            the id of shipment to find
	 * @return the matching shipment
	 */
	public abstract Shipment findByUserAndId(User user, long id);

	/**
	 * Delete a saved shipment by id.
	 * 
	 * @param id
	 *            the id of shipment to delete. Should be positive.
	 * @return true if shipment is deleted, false otherwise.
	 */
	public abstract boolean delete(long id);

	/**
	 * Save or replace a shipment.
	 * 
	 * @param shipment
	 *            the shipment to save or replace.
	 * @return true if saved successfully
	 */
	public abstract boolean save(Shipment shipment);

	/**
	 * Update a shipment.
	 * 
	 * @param update
	 *            update info for the shipment.
	 * @return true if the update is applied successfully.
	 */
	public abstract boolean update(Shipment update);

}
