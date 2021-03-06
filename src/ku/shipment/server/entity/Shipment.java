package ku.shipment.server.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

/**
 * Shipment entity.
 * 
 * @author Veerapat Threeravipark 5510547022
 * 
 */
@Entity
@Table(name = "shipments")
@XmlRootElement(name = "shipment")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(Item.class)
public class Shipment implements Serializable {
	public static final String STATUS_CREATED = "Create";
	public static final String STATUS_PACKED = "Picked Up";
	public static final String STATUS_SENDING = "In Transit";
	public static final String STATUS_RECEIVED = "Received";
	public static final String TYPE_EMS = "EMS";
	private static final long serialVersionUID = 3645343276027601559L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@XmlAttribute
	@Column(name = "shipment_id")
	private long id;
	@Column(name = "status")
	private String status;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_created_time")
	private Date status_created_time;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_packed_time")
	private Date status_packed_time;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_sending_time")
	private Date status_sending_time;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_received_time")
	private Date status_received_time;
	@Column(name = "type")
	private String type;
	@Column(name = "courier_name")
	private String courier_name;
	@Column(name = "courier_address")
	private String courier_address;
	@Column(name = "receive_name")
	private String receive_name;
	@Column(name = "receive_address")
	private String receive_address;
	@Column(name = "total_weight")
	private float total_weight;
	@Column(name = "total_cost")
	private float total_cost;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	@XmlInverseReference(mappedBy = "shipment")
	@XmlTransient
	private User user;

	@XmlElementWrapper(name = "items")
	@OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Item> item;

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

	public Date getStatus_created_time() {
		return status_created_time;
	}

	public void setStatus_created_time(Date status_created_time) {
		this.status_created_time = status_created_time;
	}

	public Date getStatus_packed_time() {
		return status_packed_time;
	}

	public void setStatus_packed_time(Date status_packed_time) {
		this.status_packed_time = status_packed_time;
	}

	public Date getStatus_sending_time() {
		return status_sending_time;
	}

	public void setStatus_sending_time(Date status_sending_time) {
		this.status_sending_time = status_sending_time;
	}

	public Date getStatus_received_time() {
		return status_received_time;
	}

	public void setStatus_received_time(Date status_received_time) {
		this.status_received_time = status_received_time;
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

	public String getReceive_name() {
		return receive_name;
	}

	public void setReceive_name(String receive_name) {
		this.receive_name = receive_name;
	}

	public String getReceive_address() {
		return receive_address;
	}

	public void setReceive_address(String receive_address) {
		this.receive_address = receive_address;
	}

	public float getTotal_cost() {
		return total_cost;
	}

	public void setTotal_cost(float total_cost) {
		this.total_cost = total_cost;
	}

	public float getTotal_weight() {
		return total_weight;
	}

	public void setTotal_weight(float total_weight) {
		this.total_weight = total_weight;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Item> getItem() {
		return item;
	}

	public void setItem(List<Item> item) {
		if(this.item == null)
			this.item = new ArrayList<Item>();
		this.item = item;
	}

	/**
	 * Add item to array list.
	 * @param item item for shipping
	 */
	public void addItem(Item item) {
		if(this.item == null)
			this.item = new ArrayList<Item>();
		this.item.add(item);
		if (item.getShipment() != this) {
			item.setShipment(this);
		}
	}

	/**
	 * Calculate total weight from all item.
	 * @return total weight
	 */
	public float calTotalWeight() {
		float total = 0;
		for (Item single_item : item) {
			total += single_item.getTotalWeight();
		}
		return total;
	}

	/**
	 * Calculate cost of shipping by type of shipment.
	 * @param weight weight of order
	 * @return cost for shipping
	 */
	public float calCostByFreightRates(float weight) {
		float cost = 0;
		if (getType().equals(TYPE_EMS)) {
			
			cost = (weight / 20 * 8) + 20;

		} else {
			cost = (weight / 20 * 8) + 4;
		}
		float remain = (float) (cost % Math.floor(cost));

		if (remain == 0) {}
		else if ( remain <= 0.25 && remain > 0 ) cost -= remain - 0.25;
		else if ( remain <= 0.50 ) cost -= remain - 0.5;
		else if ( remain <= 0.75 ) cost -= remain - 0.75;

		String s = String.format("%.2f", cost);
		return Float.parseFloat(s);
	}

	/**
	 * Update status's time.
	 * @param status status of shipping product
	 * @return true if correct update status, otherwise false
	 */
	public boolean updateStatus(String status) {
		setStatus(status);
		if (this.status.equals(STATUS_CREATED)) {
			setStatus_created_time(new Date());
			return true;
		} else if (this.status.equals(STATUS_PACKED)) {
			setStatus_packed_time(new Date());
			return true;
		} else if (this.status.equals(STATUS_RECEIVED)) {
			setStatus_received_time(new Date());
			return true;
		} else if (this.status.equals(STATUS_SENDING)) {
			setStatus_sending_time(new Date());
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("[%d]", id);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass())
			return false;
		Shipment shipment = (Shipment) other;
		return shipment.getId() == this.getId();
	}

	/**
	 * Set foreign key to item
	 */
	public void setForeignKeyToItem() {
		if (item != null) {
			for (Item single_item : item) {
				single_item.setShipment(this);
			}
		}

	}
}
