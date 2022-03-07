package skill.soft.user;

import java.sql.SQLException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/***
 * Servicio para webservice de usuario
 * @author vmacas
 *
 */
@Path("/users")
public interface UserService {

	@POST
	@Path("/login-slot")
	@Produces({ "application/json", "application/xml" })
	public Response setLoginUserSlot(@QueryParam("user") String user, 
			@QueryParam("password") String password,
			@QueryParam("ip") String ip);
	
	@POST
	@Path("register-slot")
	@Produces({"application/json", "application/xml" })
	public Response registerSlot(org.json.simple.JSONObject data) throws SQLException;
}
