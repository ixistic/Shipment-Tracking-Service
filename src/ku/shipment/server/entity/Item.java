package ku.shipment.server.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

/**
 * Item entity.
 * 
 * @author Veerapat Threeravipark 5510547022
 */
@Entity
@Table(name = "items")
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(Shipment.class)
public class Item implements Serializable {
	private static final long serialVersionUID = 5460610151721574876L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_id")
	@XmlAttribute
	private long id;
	@Column(name = "name")
	private String name;
	@Column(name = "weight")
	@XmlElement(name="weight")
	private float weightPerUnit;
	@Column(name = "quantity")
	private int quantity;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "shipment_id", referencedColumnName = "shipment_id")
	@XmlInverseReference(mappedBy = "item")
	@XmlTransient
	private Shipment shipment;

	public Item() {
	}

	public Item(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getWeight() {
		return weightPerUnit;
	}

	public void setWeight(float weight) {
		this.weightPerUnit = weight;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public float getTotalWeight(){
		return this.weightPerUnit*this.quantity;
	}

	public Shipment getShipment() {
		return shipment;
	}

	public void setShipment(Shipment shipment) {
		this.shipment = shipment;
		if (!shipment.getItem().contains(this)) {
            shipment.getItem().add(this);
        }
	}

	@Override
	public String toString() {
		return String.format("[%d]", id);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass())
			return false;
		Item item = (Item) other;
		return item.getId() == this.getId();
	}

}
