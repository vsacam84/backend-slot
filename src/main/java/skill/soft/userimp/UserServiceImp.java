package skill.soft.userimp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import org.json.JSONObject;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import skill.soft.UtilSQL.Util;
import skill.soft.UtilSQL.UtilSQL;
import skill.soft.general.SQLQuery;
import skill.soft.models.GenericUser;
import skill.soft.models.LoggedUserData;
import skill.soft.rest.filter.RestSecurityFilter;
import skill.soft.security.AESCriptography;
import skill.soft.user.UserService;
import skill.soft.util.ConnectionDBSkill;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.naming.AuthenticationException;

/***
 * Clase de implementacion de webservice
 * @author vmacas
 *
 */
public class UserServiceImp implements UserService {

	private static final Logger LOGGER = Logger.getLogger(UserServiceImp.class.getName());
	@Inject
	private ConnectionDBSkill dsConn;
	private List<LoggedUserData> listuser;
	private String id_agente;


	/***
	 * Generacion de token
	 * @param login
	 * @param roles
	 * @return
	 */
	private String issueToken(String login, String roles) {
		// Calculamos la fecha de expiración del token
		Date issueDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(issueDate);
		calendar.add(Calendar.MINUTE, 480);
		Date expireDate = calendar.getTime();

		// Creamos el token
		String jwtToken = Jwts.builder().claim("roles", roles).claim("id_agente", id_agente).setSubject(login)
				.setIssuer("skillsoft.com.ec").setIssuedAt(issueDate).setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS512, RestSecurityFilter.KEY).compact();
		return jwtToken;
	}

	/***
	 * Metodo de login de usuario
	 */
	@Override
	public Response setLoginUserSlot(String user, String password, String ip) {

		JSONObject json = new JSONObject();
		Connection connection = null;

		try {

			connection = dsConn.getConnection();
			AESCriptography aes = AESCriptography.getInstance();

			String cusuarioDecrypt = aes.decrypt(user);
			String passwordDecrypt = aes.decrypt(password);

			String roles = authenticateSlot(cusuarioDecrypt, passwordDecrypt, connection, aes, ip);

			// Si todo es correcto, generamos el token
			String token = issueToken(cusuarioDecrypt, roles);

			// Devolvemos el token en la cabecera "Authorization".

			// Se podría devolver también en la respuesta directamente.
			GenericUser userG = new GenericUser();

			userG.setCode(Response.Status.OK.getStatusCode());
			userG.setMessage("validate user");
			userG.setStatus("ok");
			userG.setToken(token);
			userG.setDetailUsers(listuser);

			return Response.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).entity(userG).build();

		} catch (Exception e) {

			e.printStackTrace();
			json.put("code", Response.Status.UNAUTHORIZED.getStatusCode());
			json.put("message", e.getMessage());
			json.put("status", "error");

			return Response.status(Response.Status.UNAUTHORIZED).entity(json).build();
		} finally {
			Util.cierreconexiones(connection);
		}
	}

	/***
	 * Metodo para recuperacion de usuario
	 * @param user
	 * @param password
	 * @param connection
	 * @param aes
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	private String authenticateSlot(String user, String password, Connection connection, AESCriptography aes, String ip)
			throws Exception {

		try {
			String Roles = "";

			UtilSQL util = new UtilSQL();
			listuser = new ArrayList<>();
			List<Object> res_sql = util.ejecutarSQL(SQLQuery.LoginSelectSlot, new Object[] { user }, connection);

			LOGGER.info("res_sql..... " + res_sql.size());

			if (!res_sql.isEmpty()) {
				for (Object objeto : res_sql) {
					Object[] res = (Object[]) objeto;

					String passDB = aes.decrypt(res[2].toString());

					if (!password.equals(passDB)) {
						throw new AuthenticationException("Usuario o Contraseña Incorrectos");
					}

					LoggedUserData userdata = new LoggedUserData();

					userdata.setUsername(res[0] != null ? res[0].toString() : "");
					userdata.setFullName(res[1] != null ? res[1].toString() : "");

					userdata.setRol_principal(res[3] != null ? res[3].toString() : "");
					userdata.setCredito("10");

					int cabe = util.ejecutaSentencia(SQLQuery.Insert_Log_Login, new Object[] { user, ip, user },
							connection);
					if (cabe == 0) {
						LOGGER.warn("No se guardo el log de inicio de session del usuario " + user);
					}

					listuser.add(userdata);

				}
			} else {
				throw new AuthenticationException("Usuario o Contraseña Incorrectos");
			}

			return Roles;

		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthenticationException(e.getMessage());
		}

	}

	/***
	 * Metodo de registro de usuario 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Response registerSlot(org.json.simple.JSONObject data) throws SQLException {

		Connection connection = null;
		UtilSQL util = new UtilSQL();
		org.json.simple.JSONObject json = new org.json.simple.JSONObject();
		try {

			connection = dsConn.getConnection();
			String id_usuario = data.get("id_usuario") == null ? null : data.get("id_usuario").toString();
			if (validateUser(connection, id_usuario, util)) {
				json.put("code", Response.Status.NOT_FOUND.getStatusCode());
				json.put("message", "Ya se encuentra registrado el usuario: " + id_usuario);
				json.put("status", Response.Status.NOT_FOUND);

				return Response.status(Response.Status.NOT_FOUND).entity(json).build();
			}

			String nombre = data.get("nombre") == null ? "" : data.get("nombre").toString();
			String contrasenia = data.get("contrasenia") == null ? "" : data.get("contrasenia").toString();
			connection.setAutoCommit(false);

			int res_sql_cuenta = util.ejecutaSentencia(SQLQuery.insertUser,
					new Object[] { id_usuario, nombre, contrasenia }, connection);

			if (res_sql_cuenta > 0) {
				LOGGER.info("Se inserto t_agente con id: " + id_agente);
			} else {
				connection.rollback();
				json.put("code", Response.Status.NOT_FOUND.getStatusCode());
				json.put("message", "No se pudo guardar");
				json.put("status", Response.Status.NOT_FOUND);

				return Response.status(Response.Status.NOT_FOUND).entity(json).build();
			}

			connection.commit();

			json.put("code", Response.Status.OK.getStatusCode());
			json.put("message", "Se Guardo Exitosamente....");
			json.put("status", Response.Status.OK);
			return Response.ok().entity(json).build();

		} catch (Exception e) {

			connection.rollback();
			e.printStackTrace();
			json.put("code", Response.Status.NOT_FOUND.getStatusCode());
			json.put("message", e.getMessage());
			json.put("status", Response.Status.NOT_FOUND);

			return Response.status(Response.Status.NOT_FOUND).entity(json).build();

		} finally {
			Util.cierreconexiones(connection);
		}
	}

	/***
	 * Metodo validacion de usuario 
	 * @param connection
	 * @param id_usuario
	 * @param util
	 * @return
	 * @throws Exception
	 */
	private boolean validateUser(Connection connection, String id_usuario, UtilSQL util) throws Exception {

		List<Object> res_sql = util.ejecutarSQL(SQLQuery.validateUser, new Object[] { id_usuario }, connection);

		LOGGER.info("res_sql..... " + res_sql.size());
		if (!res_sql.isEmpty()) {

			int res = Integer.parseInt(res_sql.get(0).toString());

			if (res == 0) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

}
