package skill.soft.implement;

import java.sql.SQLException;
import java.util.Random;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import skill.soft.service.SlotService;

public class SlotWS implements SlotService {

	private static final Logger LOGGER = Logger.getLogger(SlotWS.class.getName());

	/***
	 * Metodo para generar la combinacion de los resultados del tragamonedas
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public Response game(SecurityContext secContext, JSONObject data) throws SQLException {

		JSONObject json = new JSONObject();

		try {

			Integer credito = data.get("credito") == null ? 0 : Integer.valueOf(data.get("credito").toString());

			String cadena = gameRamdom();

			// 30%
			if (credito >= 40 && credito <= 60) {
				if (probability30()) {
					cadena = gameRamdom();
				}
			}

			// 60%
			if (credito > 60) {
				if (probability60()) {
					cadena = gameRamdom();
				}
			}

			boolean decrementar = true;

			// Cereza
			if ("111".equals(cadena)) {
				credito = credito + 10;
				decrementar = false;
			}

			// Limon
			if ("222".equals(cadena)) {
				credito = credito + 20;
				decrementar = false;
			}

			// Naranja
			if ("333".equals(cadena)) {
				credito = credito + 30;
				decrementar = false;
			}

			// Sandia
			if ("444".equals(cadena)) {
				credito = credito + 40;
				decrementar = false;
			}

			if (decrementar) {
				credito = credito - 1;
			}

			json.put("code", Response.Status.OK.getStatusCode());
			json.put("message", "Game Start");
			json.put("status", Response.Status.OK);
			json.put("credito", credito);
			json.put("valor", cadena);
			return Response.ok().entity(json).build();

		} catch (Exception e) {
			e.printStackTrace();
			json.put("code", Response.Status.NOT_FOUND.getStatusCode());
			json.put("message", e.getMessage());
			json.put("status", Response.Status.NOT_FOUND);

			return Response.status(Response.Status.NOT_FOUND).entity(json).build();

		}
	}

	/***
	 * Generacion del numero ramdomico
	 * 
	 * @return
	 */
	private String gameRamdom() {

		String cadena = "";
		int min = 1;
		int max = 3;

		for (int i = 0; i < 3; i++) {
			Random random = new Random();

			int value = random.nextInt(max + min) + min;
			LOGGER.info(value);

			cadena = cadena + value;
		}

		return cadena;
	}

	/***
	 * Probabilidad del 30%
	 * 
	 * @return
	 */
	private Boolean probability30() {

		int min = 1;
		int max = 10;

		Random random = new Random();
		int value = random.nextInt(max + min) + min;
		LOGGER.info(value);

		if (value == 1 || value == 2 || value == 3) {
			return true;
		} else {
			return false;
		}
	}

	/***
	 * Probabilidad del 60%
	 * 
	 * @return
	 */
	private Boolean probability60() {

		int min = 1;
		int max = 10;

		Random random = new Random();
		int value = random.nextInt(max + min) + min;
		LOGGER.info(value);

		if (value == 1 || value == 2 || value == 3 || value == 4 || value == 5 || value == 6) {
			return true;
		} else {
			return false;
		}
	}

}
