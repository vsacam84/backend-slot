package skill.soft.rest.filter;

import java.io.IOException;
import java.security.Key;
import java.util.Arrays;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.crypto.MacProvider;
import skill.soft.annotation.Secured;
import skill.soft.bean.User;
import skill.soft.com.ec.security.MyApplicationSecurityContext;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class RestSecurityFilter implements ContainerRequestFilter {

	public static final Key KEY = MacProvider.generateKey();

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Recupera la cabecera HTTP Authorization de la petición
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		try {
			// Extrae el token de la cabecera
			String token = authorizationHeader.substring("Bearer".length()).trim();

			// Valida el token utilizando la cadena secreta
			Jws<Claims> claims = Jwts.parser().setSigningKey(KEY).parseClaimsJws(token);

			// Creamos el usuario a partir de la información del token
			User usuario = new User();
			usuario.setUsername(claims.getBody().getSubject());
			String roles = (String) claims.getBody().get("roles");
			usuario.setRoles(Arrays.asList(roles.split(",")));

			Object id = claims.getBody().get("id_agente");
			if (id != null) {
				usuario.setId_agente(id.toString());
			} else {
				usuario.setId_agente("");
			}

			// Creamos el SecurityContext
			MyApplicationSecurityContext secContext = new MyApplicationSecurityContext(usuario,
					requestContext.getSecurityContext().isSecure());

			// Seteamos el contexto de seguridad
			requestContext.setSecurityContext(secContext);

		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}
}
