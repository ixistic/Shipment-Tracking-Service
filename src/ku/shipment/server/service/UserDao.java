package ku.shipment.server.service;

import java.util.List;

import ku.shipment.server.entity.User;

public interface UserDao {

	public abstract User find(long id);

	public abstract List<User> findAll();
	
	public abstract User findByAccessToken(String accessToken);
	
	public abstract boolean delete(long id);
	
	public abstract boolean save(User user);
	
	public abstract boolean update(User update);
	
	
}

