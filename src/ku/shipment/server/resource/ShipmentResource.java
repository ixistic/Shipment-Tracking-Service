package ku.shipment.server.resource;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import ku.shipment.oauth.OAuthTokenResponse;
import ku.shipment.server.entity.Shipment;
import ku.shipment.server.entity.Shipments;
import ku.shipment.server.entity.User;
import ku.shipment.server.service.ShipmentDao;
import ku.shipment.server.service.ShipmentDaoFactory;
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
import org.json.JSONArray;
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

	private final String CLIENT_ID = "229216041764-sjdasnqgvom7lcva4fni6nrcpid4fv7u.apps.googleusercontent.com";
	private final String CLIENT_SECRET = "wD1SIj5DdJiO5d4qz7FVL0Ko";

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

	/**
	 * 
	 * @return
	 */
	@GET
	@Path("/auth")
	public Response authenticate() {
		try {
			request = OAuthClientRequest
					.authorizationProvider(OAuthProviderType.GOOGLE)
					.setClientId(CLIENT_ID)
					.setResponseType("code")
					.setScope("email")
					.setRedirectURI(
							UriBuilder.fromUri(uriInfo.getBaseUri())
									.path("shipments/oauth2callback").build()
									.toString()).buildQueryMessage();

			URI redirect = new URI(request.getLocationUri());
			return Response.seeOther(redirect).build();
		} catch (OAuthSystemException e) {
			throw new WebApplicationException(e);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e);
		}
	}

	/**
	 * 
	 * @param code
	 * @param state
	 * @return
	 */
	@GET
	@Path("/oauth2callback")
	public Response authorize(@QueryParam("code") String code,
			@QueryParam("state") String state) {
		// path to redirect after authorization

		String accessToken = "";
		String email = "";

		try {
			// Request to exchange code for access token and id token
			request = OAuthClientRequest
					.tokenProvider(OAuthProviderType.GOOGLE)
					.setCode(code)
					.setClientId(CLIENT_ID)
					.setClientSecret(CLIENT_SECRET)
					.setRedirectURI(
							UriBuilder.fromUri(uriInfo.getBaseUri())
									.path("shipments/oauth2callback").build()
									.toString())
					.setGrantType(GrantType.AUTHORIZATION_CODE)
					.buildBodyMessage();

			client = new OAuthClient(new URLConnectionClient());
			response = client.accessToken(request, OAuthTokenResponse.class);

			// Get the access token from the response
			accessToken = ((OAuthJSONAccessTokenResponse) response)
					.getAccessToken();

			email = getEmail(((OAuthResourceResponse) getClientResource(accessToken))
					.getBody());
			User oldUser = userDao.findByEmail(email);
			if (oldUser != null) {
				oldUser.setAccessToken(accessToken);
				userDao.update(oldUser);
			} else {
				User user = new User();
				user.setAccessToken(accessToken);
				user.setEmail(email);
				userDao.save(user);
			}

			// Add code to notify application of authenticated user
		} catch (OAuthProblemException | OAuthSystemException e) {
			e.printStackTrace();
		}

		final URI uri = uriInfo.getBaseUriBuilder()
				.path("shipments/" + accessToken).build();

		return Response.seeOther(uri).build();
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	@GET
	@Path("{accessToken}")
	public Response getAccessToken(@PathParam("accessToken") String token) {
		URI uri = null;
		try {
			uri = new URI("http://158.108.139.141/web/index.php?accessToken="
					+ token);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return Response.seeOther(uri).entity(token).build();
	}

	/**
	 * 
	 * @param accessToken
	 * @return
	 */
	public OAuthClientResponse getClientResource(String accessToken) {
		try {
			request = new OAuthBearerClientRequest(
					"https://www.googleapis.com/plus/v1/people/me")
					.setAccessToken(accessToken).buildQueryMessage();
			return client.resource(request, OAuth.HttpMethod.GET,
					OAuthResourceResponse.class);
		} catch (OAuthSystemException | OAuthProblemException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param body
	 * @return
	 */
	public String getEmail(String body) {

		Map<String, Object> params = new HashMap<String, Object>();
		try {
			JSONObject obj = new JSONObject(body);
			Iterator<?> it = obj.keys();
			while (it.hasNext()) {
				Object o = it.next();
				if (o instanceof String) {
					String key = (String) o;
					params.put(key, obj.get(key));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			JSONArray arr = new JSONArray(params.get("emails").toString());
			String j = arr.get(0).toString();
			JSONObject json = new JSONObject(j);
			return json.getString("value");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param request
	 * @param accept
	 * @param accessToken
	 * @param auth
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getShipments(@Context Request request,
			@HeaderParam("Accept") String accept,
			@HeaderParam("Authorization") String accessToken,
			@QueryParam("auth_key") String auth) {
		if (auth != null) {
			accessToken = auth;
		}
		if (accessToken == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User user = userDao.findByAccessToken(accessToken);
		if (user != null) {
			GenericEntity<List<Shipment>> ge = null;
			ge = convertListToGE(shipmentDao.findAll());

			if (!ge.getEntity().isEmpty()) {
				System.out.println("GET ALL");
				// xml
				if (accept.equals(MediaType.APPLICATION_XML)) {
					return Response.ok(ge).build();
				}
				// default json
				Shipments shipment = new Shipments();
				shipment.setShipments(shipmentDao.findAll());
				String response = convertXMLtoJSON(mashallXml(shipment));
				return Response.ok(response, MediaType.APPLICATION_JSON)
						.build();
			}
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.status(Response.Status.UNAUTHORIZED).build();
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
			@Context Request request, @HeaderParam("Accept") String accept,
			@HeaderParam("Authorization") String accessToken,
			@QueryParam("auth_key") String auth) {
		if (auth != null) {
			accessToken = auth;
		}
		if (accessToken == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User user = userDao.findByAccessToken(accessToken);
		if (user != null) {
			Shipment shipment = shipmentDao.find(id);
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
		return Response.status(Response.Status.UNAUTHORIZED).build();
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
			@Context UriInfo uriInfo,
			@HeaderParam("Authorization") String accessToken,
			@QueryParam("auth_key") String auth) {
		if (auth != null) {
			accessToken = auth;
		}
		if (accessToken == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User user = userDao.findByAccessToken(accessToken);
		if (user != null) {
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
		return Response.status(Response.Status.UNAUTHORIZED).build();
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
			@Context Request request,
			@HeaderParam("Authorization") String accessToken,
			@QueryParam("auth_key") String auth) {
		if (auth != null) {
			accessToken = auth;
		}
		if (accessToken == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User user = userDao.findByAccessToken(accessToken);
		if (user != null) {
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
		return Response.status(Response.Status.UNAUTHORIZED).build();
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
			@Context UriInfo uriInfo, @Context Request request,
			@HeaderParam("Authorization") String accessToken,
			@QueryParam("auth_key") String auth) {
		if (auth != null) {
			accessToken = auth;
		}
		if (accessToken == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User user = userDao.findByAccessToken(accessToken);
		if (user != null) {
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
		return Response.status(Response.Status.UNAUTHORIZED).build();
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
