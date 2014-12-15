package ku.shipment.server.service;

import java.util.List;

import ku.shipment.server.entity.User;

/**
 * Interface defines the operations required by a DAO for user.
 * 
 * @author veerapat
 * 
 */
public interface UserDao {

	/**
	 * Find a user by ID in users.
	 * 
	 * @param the
	 *            id of user to find
	 * @return the matching user or null if the id is not found
	 */
	public abstract User find(long id);

	/**
	 * Return all the persisted users as a List. There is no guarantee what
	 * implementation of List is returned, so caller should use only List
	 * methods (not, say ArrayList).
	 * 
	 * @return list of all users in persistent storage. If no users, returns an
	 *         empty list.
	 */
	public abstract List<User> findAll();

	/**
	 * Find shipment by access token.
	 * 
	 * @param accessToken
	 *            access token
	 * @return the matching shipment
	 */
	public abstract User findByAccessToken(String accessToken);

	/**
	 * Find shipment by email.
	 * 
	 * @param email
	 *            user's email
	 * @return the matching shipment
	 */
	public abstract User findByEmail(String email);

	/**
	 * Delete a saved user by id.
	 * 
	 * @param id
	 *            the id of user to delete. Should be positive.
	 * @return true if user is deleted, false otherwise.
	 */
	public abstract boolean delete(long id);

	/**
	 * Save or replace a user.
	 * 
	 * @param user
	 *            the user to save or replace.
	 * @return true if saved successfully
	 */
	public abstract boolean save(User user);

	/**
	 * Update a user.
	 * 
	 * @param update
	 *            update info for the user.
	 * @return true if the update is applied successfully.
	 */
	public abstract boolean update(User update);

}
