package ku.shipment.server.resource;

import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import ku.shipment.server.entity.Shipment;
import ku.shipment.server.service.DaoFactory;
import ku.shipment.server.service.ShipmentDao;


@Path("/shipments")
@Singleton
public class ShipmentResource {
	private ShipmentDao dao;
	private CacheControl cc;
	@Context
	private UriInfo uriInfo;

	/**
	 * Construct ShipmentDao from DaoFactory.
	 */
	public ShipmentResource() {
		cc = new CacheControl();
		cc.setMaxAge(46800);
		dao = DaoFactory.getInstance().getShipmentDao();
		System.out.println("Initial ShipmentDao.");
	}

	/**
	 * Get a list of all shipment
	 * 
	 * @param query
	 *            is query string (title)
	 * @return response 200 OK if result not null that show list of result
	 *         shipment. If result is null response 404 NOT FOUND
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getProduct(@Context Request request) {
		GenericEntity<List<Shipment>> ge = null;
		ge = convertListToGE(dao.findAll());
		if (!ge.getEntity().isEmpty()) {
			return Response.ok(ge).build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();

	}

	/**
	 * Get a shipment by id.
	 * 
	 * @param id
	 *            identifier of shipment
	 * @return response 200 OK if result not null that show shipment. If result
	 *         is null response 404 NOT FOUND
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getProductById(@PathParam("id") long id,
			@Context Request request) {
		Shipment product = dao.find(id);
		if (product == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		EntityTag etag = attachEtag(product);
		ResponseBuilder builder = request.evaluatePreconditions(etag);
		if (builder == null) {
			builder = Response.ok(product);
			builder.tag(etag);
		}
		builder.cacheControl(cc);
		return builder.build();
	}
	/**
	 * Create an instance directly by supplying the generic type information
	 * with the entity.
	 * 
	 * @param shipments
	 *            list of shipment
	 * @return generic entity
	 */
	public GenericEntity<List<Shipment>> convertListToGE(List<Shipment> shipments) {
		GenericEntity<List<Shipment>> ge = new GenericEntity<List<Shipment>>(shipments) {
		};
		return ge;
	}

	/**
	 * Construct Etag from shipment
	 * 
	 * @param shipment
	 * @return etag Entity tag of shipment
	 */
	public EntityTag attachEtag(Shipment shipment) {
		EntityTag etag = new EntityTag(shipment.sha1());
		return etag;
	}
}
