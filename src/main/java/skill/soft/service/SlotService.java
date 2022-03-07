package skill.soft.service;

import java.sql.SQLException;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.json.simple.JSONObject;

import skill.soft.annotation.Secured;


/***
 * Servicio de implementacion de webservice
 * @author vmacas
 *
 */

@Path("/slot")
public interface SlotService {
	
	
	@POST
	@Path("game")
	@Secured
	@Produces({"application/json", "application/xml" })
	public Response game(@Context SecurityContext secContext,
			JSONObject data) throws SQLException;


}
