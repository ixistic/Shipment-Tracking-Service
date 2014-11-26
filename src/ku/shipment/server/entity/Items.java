package ku.shipment.server.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "items")
@XmlAccessorType(XmlAccessType.FIELD)
public class Items {

	@XmlElement(name = "item")
	private List<Item> items;

	public List<Item> getContacts() {
		return items;
	}

	public void setProducts(List<Item> items) {
		this.items = items;
	}
}
