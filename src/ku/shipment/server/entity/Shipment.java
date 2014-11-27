package ku.shipment.server.entity;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.xml.bind.annotation.XmlSeeAlso;

@Entity
@Table(name = "shipments")
@XmlRootElement(name = "shipment")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(Item.class)
public class Shipment implements Serializable {
	private static final long serialVersionUID = 3645343276027601559L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlAttribute
	@Column(name="id")
	private long id;
	@Column(name="status")
	private String status;
	@Column(name="status_created_time")
	private String status_created_time;
	@Column(name="status_packed_time")
	private String status_packed_time;
	@Column(name="status_sending_time")
	private String status_sending_time;
	@Column(name="status_recieved_time")
	private String status_recieved_time;
	@Column(name="type")
	private String type;
	@Column(name="courier_name")
	private String courier_name;
	@Column(name="courier_address")
	private String courier_address;
	@Column(name="recieve_name")
	private String recieve_name;
	@Column(name="recieve_address")
	private String recieve_address;
	@Column(name="total_cost")
	private float total_cost;
	
	@XmlElementWrapper(name = "items")
	@OneToMany(mappedBy="shipment") 
	private List<Item> item;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Shipment() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus_created_time() {
		return status_created_time;
	}

	public void setStatus_created_time(String status_created_time) {
		this.status_created_time = status_created_time;
	}

	public String getStatus_packed_time() {
		return status_packed_time;
	}

	public void setStatus_packed_time(String status_packed_time) {
		this.status_packed_time = status_packed_time;
	}

	public String getStatus_sending_time() {
		return status_sending_time;
	}

	public void setStatus_sending_time(String status_sending_time) {
		this.status_sending_time = status_sending_time;
	}

	public String getStatus_recieved_time() {
		return status_recieved_time;
	}

	public void setStatus_recieved_time(String status_recieved_time) {
		this.status_recieved_time = status_recieved_time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCourier_name() {
		return courier_name;
	}

	public void setCourier_name(String courier_name) {
		this.courier_name = courier_name;
	}

	public String getCourier_address() {
		return courier_address;
	}

	public void setCourier_address(String courier_address) {
		this.courier_address = courier_address;
	}

	public String getRecieve_name() {
		return recieve_name;
	}

	public void setRecieve_name(String recieve_name) {
		this.recieve_name = recieve_name;
	}

	public String getRecieve_address() {
		return recieve_address;
	}

	public void setRecieve_address(String recieve_address) {
		this.recieve_address = recieve_address;
	}

	public float getTotal_cost() {
		return total_cost;
	}

	public void setTotal_cost(float total_cost) {
		this.total_cost = total_cost;
	}

	
	public List<Item> getItems() {
		return item;
	}

	public void setItems(List<Item> item) {
		this.item = item;
	}

	@Override
	public String toString() {
		return String.format("[%d]", id);
	}

	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass())
			return false;
		Shipment product = (Shipment) other;
		return product.getId() == this.getId();
	}


	/**
	 * Test if a string is null or only whitespace.
	 * 
	 * @param arg
	 *            the string to test
	 * @return true if string variable is null or contains only whitespace
	 */
	private static boolean isEmpty(String arg) {
		return arg == null || arg.matches("\\s*");
	}

	/**
	 * Construct sha1(secure hash) of a text string.
	 * @return string string of sha1
	 */
	public String sha1() {
		int text = this.hashCode(); 
		String input = ""+id+text;
        MessageDigest mDigest = null;
		try {
			mDigest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
         
        return sb.toString();
    }
}
