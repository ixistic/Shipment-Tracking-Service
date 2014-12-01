package ku.shipment.server.resource;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import ku.shipment.oauth.OAuthTokenResponse;
import ku.shipment.server.entity.Shipment;
import ku.shipment.server.entity.Shipments;
import ku.shipment.server.service.ShipmentDaoFactory;
import ku.shipment.server.service.ShipmentDao;
import ku.shipment.server.service.UserDao;
import ku.shipment.server.service.UserDaoFactory;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthClientResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

@Path("/shipments")
@Singleton
public class ShipmentResource {
	private ShipmentDao shipmentDao;
	private UserDao userDao;
	private CacheControl cc;
	@Context
	private UriInfo uriInfo;
	
	private OAuthClient client;
	private OAuthClientRequest request;
	private OAuthClientResponse response;
	private String REDIRECT_URI = UriBuilder.fromUri(uriInfo.getBaseUri())
			.path("oauth2callback").build().toString();
	private String ACCESS_TOKEN;
	private final String CLIENT_ID = "195639712959-mav09j1h8glutoqbhubnknkdcn9slvca.apps.googleusercontent.com";
	private final String CLIENT_SECRET = "nt9fPMyv26sxzWUS0MX_IPa5";
	

	/**
	 * Construct ShipmentDao from DaoFactory.
	 */
	public ShipmentResource() {
		cc = new CacheControl();
		cc.setMaxAge(46800);
		shipmentDao = ShipmentDaoFactory.getInstance().getShipmentDao();
		userDao = UserDaoFactory.getInstance().getUserDao();
		System.out.println("Initial ShipmentDao.");
	}
	
	@GET
	@Path("/google")
	public Response authenticate() {
		try {
			request = OAuthClientRequest
					.authorizationProvider(OAuthProviderType.GOOGLE)
					.setClientId(CLIENT_ID)
					.setResponseType("code")
					.setScope(
							"email")
					.setRedirectURI( REDIRECT_URI
							)
					.buildQueryMessage();
			
			URI redirect = new URI(request.getLocationUri());
			return Response.seeOther(redirect).build();
		} catch (OAuthSystemException e) {
			throw new WebApplicationException(e);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e);
		}
	}

	@GET
	@Path("/oauth2callback")
	public Response authorize(@QueryParam("code") String code,
			@QueryParam("state") String state) {
		// path to redirect after authorization
		final URI uri = uriInfo.getBaseUriBuilder().path("").build();

		try {
			// Request to exchange code for access token and id token
			request = OAuthClientRequest
					.tokenProvider(OAuthProviderType.GOOGLE)
					.setCode(code)
					.setClientId(CLIENT_ID)
					.setClientSecret(CLIENT_SECRET)
					.setRedirectURI( REDIRECT_URI
							)
					.setGrantType(GrantType.AUTHORIZATION_CODE)
					.buildBodyMessage();
			
			client = new OAuthClient(new URLConnectionClient());
			response = client
					.accessToken(request, OAuthTokenResponse.class);
			
			// Get the access token from the response
			ACCESS_TOKEN = ((OAuthJSONAccessTokenResponse) response).getAccessToken();

			
			System.out.println(((OAuthResourceResponse) getClientResource()).getBody());
			
			// Add code to notify application of authenticated user
		} catch (OAuthProblemException | OAuthSystemException e) {
			e.printStackTrace();
		} 

		return Response.seeOther(uri).build();
	}
	
	public OAuthClientResponse getClientResource() {
		try {
			request  = new OAuthBearerClientRequest("https://www.googleapis.com/plus/v1/people/me").setAccessToken(ACCESS_TOKEN).buildQueryMessage();
			return client.resource(request, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
		} catch (OAuthSystemException | OAuthProblemException e) {
			e.printStackTrace();
		}
		return null;
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
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getShipments(@Context Request request,
			@HeaderParam("Accept") String accept) {
		GenericEntity<List<Shipment>> ge = null;
		ge = convertListToGE(shipmentDao.findAll());

		if (!ge.getEntity().isEmpty()) {
			// json
			if (accept.equals(MediaType.APPLICATION_JSON)) {
				Shipments shipment = new Shipments();
				shipment.setShipments(shipmentDao.findAll());
				String response = convertXMLtoJSON(mashallXml(shipment));
				return Response.ok(response, MediaType.APPLICATION_JSON)
						.build();
			}
			// xml
			return Response.ok(ge).build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();

	}

	/**
	 * Get a shipment by id for admin.
	 * 
	 * @param id
	 *            identifier of shipment
	 * @return response 200 OK if result not null that show shipment. If result
	 *         is null response 404 NOT FOUND
	 */
	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getShipmentById(@PathParam("id") long id,
			@Context Request request, @HeaderParam("Accept") String accept) {
		Shipment shipment = shipmentDao.find(id);
		System.out.println(shipment.getItem().size());
		if (shipment == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		EntityTag etag = attachEtag(shipment);
		ResponseBuilder builder = request.evaluatePreconditions(etag);
		if (builder == null) {
			// json
			if (accept.equals(MediaType.APPLICATION_JSON)) {
				String response = convertXMLtoJSON(mashallXml(shipment));
				builder = Response.ok(response, MediaType.APPLICATION_JSON);
			}
			// xml
			else {
				builder = Response.ok(shipment);
			}
			builder.tag(etag);
		}
		builder.cacheControl(cc);
		return builder.build();
	}

	/**
	 * Get a shipment by id for user.
	 * 
	 * @param id
	 *            identifier of shipment
	 * @return response 200 OK if result not null that show shipment. If result
	 *         is null response 404 NOT FOUND
	 */
	@GET
	@Path("{id}/status")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getStatusById(@PathParam("id") long id,
			@Context Request request, @HeaderParam("Accept") String accept) {
		Shipment shipment = shipmentDao.find(id);
		
		if (shipment == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		Shipment status = new Shipment();
		status.setId(shipment.getId());
		status.setRecieve_name(shipment.getRecieve_name());
		status.setRecieve_address(shipment.getRecieve_address());
		status.setStatus(shipment.getStatus());
		status.setStatus_created_time(shipment.getStatus_created_time());
		status.setStatus_packed_time(shipment.getStatus_packed_time());
		status.setStatus_sending_time(shipment.getStatus_sending_time());
		status.setStatus_recieved_time(shipment.getStatus_recieved_time());
		status.setTotal_weight(shipment.getTotal_weight());
		status.setTotal_cost(shipment.getTotal_cost());
		// json  
		if (accept.equals(MediaType.APPLICATION_JSON)) {
			String response = convertXMLtoJSON(mashallXml(status));
			return Response.ok(response, MediaType.APPLICATION_JSON).build();
		}
		// xml
		else {
			return Response.ok(status).build();
		}

	}

	/**
	 * Update a shipment. Only update the attributes supplied in request body.
	 * 
	 * @param id
	 *            identifier of shipment
	 * @param element
	 *            xml file in JAXBElement for unmarshal data
	 * @return response 200 OK if shipment can update, if invalid data response
	 *         400 BAD REQUEST, otherwise response 404 NOT FOUND
	 */
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response putContact(@PathParam("id") long id,
			JAXBElement<Shipment> element, @Context Request request,
			@Context UriInfo uriInfo) {
		Shipment newStatus = element.getValue();
		if (!(newStatus.getId() == id)) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		Shipment shipment = shipmentDao.find(id);
		shipment.updateStatus(newStatus.getStatus());
		EntityTag etag = attachEtag(shipment);
		ResponseBuilder builder = request.evaluatePreconditions(etag);
		if (builder == null) {
			if (!shipmentDao.update(shipment)) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			builder = Response.ok();
			builder.tag(etag);
		}
		builder.cacheControl(cc);
		return builder.build();
	}

	/**
	 * Delete a shipment with matching id
	 * 
	 * @param id
	 *            identifier of shipment
	 * @return response 200 OK if shipment can delete, otherwise response 404
	 *         NOT FOUND
	 */
	@DELETE
	@Path("{id}")
	public Response deleteShipment(@PathParam("id") long id,
			@Context Request request) {
		Shipment shipment = shipmentDao.find(id);
		if (shipment == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		EntityTag etag = attachEtag(shipment);
		ResponseBuilder builder = request.evaluatePreconditions(etag);
		if (builder == null) {
			shipmentDao.delete(id);
			builder = Response.ok();
		}
		builder.cacheControl(cc);
		return builder.build();
	}

	/**
	 * Create a new shipment. If shipment's id is omitted or 0, the server will
	 * assign a unique ID and return it as the Location header.
	 * 
	 * @param element
	 *            element of JAXBElement
	 * @param uriInfo
	 *            information of URI
	 * @return response 201 CREATED if create success that show location header.
	 *         If same id response 409 CONFLICT, otherwise 400 BAD REQUEST
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_XML })
	public Response post(JAXBElement<Shipment> element,
			@Context UriInfo uriInfo, @Context Request request) {
		Shipment shipment = element.getValue();
		shipment.setTotal_weight(shipment.calTotalWeight());
		shipment.setTotal_cost(shipment.calCostByFreightRates(shipment
				.getTotal_weight()));
		shipment.updateStatus(Shipment.STATUS_CREATED);
		shipment.setForeignKeyToItem();
		if (shipmentDao.find(shipment.getId()) != null) {
			return Response.status(Response.Status.CONFLICT).build();
		}
		EntityTag etag = attachEtag(shipment);
		ResponseBuilder builder = request.evaluatePreconditions(etag);
		if (builder == null) {
			if (!shipmentDao.save(shipment)) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			URI uri = uriInfo.getAbsolutePathBuilder()
					.path(shipment.getId() + "").build();
			builder = Response.created(uri);
			builder.tag(etag);
		}
		builder.cacheControl(cc);
		return builder.build();
	}

	/**
	 * Get cost for shipment.
	 * 
	 * @param id
	 *            identifier of shipment
	 * @return response 200 OK if result not null. If result is null response
	 *         500 bad request
	 */
	@POST
	@Path("/calculate")
	@Consumes({ MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response calculate(JAXBElement<Shipment> element,
			@Context Request request, @HeaderParam("Accept") String accept) {
		Shipment shipment = element.getValue();
		shipment.setTotal_weight(shipment.calTotalWeight());
		shipment.setTotal_cost(shipment.calCostByFreightRates(shipment
				.getTotal_weight()));
		Shipment answer = new Shipment();
		answer.setType(shipment.getType());
		answer.setTotal_weight(shipment.getTotal_weight());
		answer.setTotal_cost(shipment.getTotal_cost());
		// json
		if (accept.equals(MediaType.APPLICATION_JSON)) {
			String response = convertXMLtoJSON(mashallXml(answer));
			return Response.ok(response, MediaType.APPLICATION_JSON).build();
		}
		// xml
		else {
			return Response.ok(answer).build();
		}
	}

	/**
	 * Create an instance directly by supplying the generic type information
	 * with the entity.
	 * 
	 * @param shipments
	 *            list of shipment
	 * @return generic entity
	 */
	public GenericEntity<List<Shipment>> convertListToGE(
			List<Shipment> shipments) {

		GenericEntity<List<Shipment>> ge = new GenericEntity<List<Shipment>>(
				shipments) {
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

	public String mashallXml(Shipment shipment) {
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext
					.newInstance(ku.shipment.server.entity.Shipment.class);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// Marshal the employees list in console
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(shipment, sw);
			return sw.toString();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String mashallXml(Shipments shipments) {
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext
					.newInstance(ku.shipment.server.entity.Shipments.class);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// Marshal the employees list in console
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(shipments, sw);
			return sw.toString();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String convertXMLtoJSON(String xml) {
		int PRETTY_PRINT_INDENT_FACTOR = 4;
		JSONObject xmlJSONObj;
		try {
			xmlJSONObj = XML.toJSONObject(xml);

			String jsonPrettyPrintString = xmlJSONObj
					.toString(PRETTY_PRINT_INDENT_FACTOR);
			return jsonPrettyPrintString;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}
