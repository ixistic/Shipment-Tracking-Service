package ku.shipment.server.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "users")
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User implements Serializable{
	public static final int TYPE_DELIVERY_PERSON = 1;
	public static final int TYPE_CUSTOMER = 0;
	private static final long serialVersionUID = 6979236188240217830L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	@XmlAttribute
	private long id;
	@Column(name = "email")
	private String email;
	@Column(name = "accessToken")
	private String accessToken;
	//default 0 : customer
	@Column(name = "type")
	private int type;
	
	@XmlElementWrapper(name = "shipments")
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Shipment> shipment = new ArrayList<Shipment>();
	
	public User(){
		
	}
	
	public User(String email,String accessToken,int type){
		this.email = email;
		this.accessToken = accessToken;
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public List<Shipment> getShipment() {
		return shipment;
	}

	public void setShipment(List<Shipment> shipments) {
		this.shipment = shipments;
	}
	
	public void addShipment(Shipment shipment){
		this.shipment.add(shipment);
	}

	@Override
	public String toString() {
		return String.format("[%d]", id);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass())
			return false;
		User user = (User) other;
		return user.getId() == this.getId();
	}
	
	public void setForeignKeyToShipment() {
		if (shipment != null) {
			for (Shipment single_shipment : shipment) {
				single_shipment.setUser(this);
			}
		}

	}
}
