package skill.soft.UtilSQL;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.util.Precision;

/***
 * CLASE QUE PERMITE EL CIERRE DE CONEXIONES DE MANERA GENERICA
 * 
 * @author COOPCREA
 *
 */
public class Util {

	/** VARIABLE QUE PERMITE REALIZAR EL MANEJO DE LOGS **/
	private final static Logger LOGGER = Logger.getLogger(Util.class.getName());

	/***
	 * METODO QUE REALIZA EL CIERRE DE CONEXIONES
	 * 
	 * @param coonCREATurnos
	 */
	public static void cierreconexiones(Connection coon) {

		try {
			if (coon != null && !coon.isClosed())
				coon.close();
		} catch (Exception e) {
			LOGGER.info("ERROR AL CERRAR CONEXION BD CREA TURNOS");
			e.printStackTrace();
		}
	}

	public Util() {
		super();
		// TODO Auto-generated constructor stub
	}

	/***
	 * METODO QUE PERMITE REALIZAR CERRAR LOS RESULTSET
	 * 
	 * @param ResultSet
	 */
	public static void cierreResultSet(ResultSet ResultSet) {
		try {
			if (ResultSet != null)
				ResultSet.close();
		} catch (SQLException e) {
			LOGGER.info("ERROR AL CERRAR RESULTSET");
			e.printStackTrace();
		}
	}

	/***
	 * METODO QUE PERMITE CERRAR LOS PREPAREDESTATEMENT
	 * 
	 * @param preparedStatement
	 */
	public static void cierrePreparedStatement(PreparedStatement preparedStatement) {
		try {
			if (preparedStatement != null)
				preparedStatement.close();
		} catch (SQLException e) {
			LOGGER.info("ERROR AL CERRAR PREPAREDSTATEMENT");
			e.printStackTrace();
		}
	}

	/***
	 * METODO QUE CONVIERTE STRING EN DATE
	 * 
	 * @param fecha
	 * @param formato
	 * @return
	 */
	public static Date getDateFormatSql(String fecha, String formato) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(formato);
			java.util.Date parsed = format.parse(fecha);
			return new Date(parsed.getTime());
		} catch (ParseException e) {
			LOGGER.info("ERROR AL CONVERTIR FECHA, FORMATO INCORRECTO: " + e.getMessage());
			return null;
		}
	}

	/***
	 * Metodo para separara los nombres y apellidos
	 * 
	 * @param nommbre
	 * @return
	 */
	public String[] getInformation(String nombres) {

		String[] namesPerson = new String[2];
		String nombresSeparado = "";// USADA PARA ALMACENAR NOMBRES EN BASE DE DATOS COOPERATIVA
		String apellidoSeparado = "";// USADA PARA ALMACENAR APELLIDOS EN BASE DE DATOs DE COOPERATIVA

		/**
		 * PROCESO PARA SEPARAR NOMBRES Y APELLIDOS DE RESPUESTA WS DE REGISTRO CIVIL
		 **/
		String nombreCompleto[] = nombres.split(" ");

		int size = nombreCompleto.length;// INDICA TAMAÑO DEL NOMBRE COMPLETO DE LA PERSONA

		if (size > 3) {
			if (size > 4) {
				if (size > 6) {
					for (int i = size - 4; i >= 0; i--) {
						apellidoSeparado = nombreCompleto[i] + " " + apellidoSeparado;
					}
					nombresSeparado = nombreCompleto[size - 3] + " " + nombreCompleto[size - 2] + " "
							+ nombreCompleto[size - 1];
				} else {
					for (int i = size - 3; i >= 0; i--) {
						apellidoSeparado = nombreCompleto[i] + " " + apellidoSeparado;
					}
					nombresSeparado = nombreCompleto[size - 2] + " " + nombreCompleto[size - 1];
				}
			} else {
				apellidoSeparado = nombreCompleto[0] + " " + nombreCompleto[1];
				nombresSeparado = nombreCompleto[2] + " " + nombreCompleto[3];
			}
		} else {
			apellidoSeparado = nombreCompleto[0] + " " + nombreCompleto[1];
			nombresSeparado = nombreCompleto[2];
		}

		namesPerson[0] = apellidoSeparado;
		namesPerson[1] = nombresSeparado;

		return namesPerson;
	}

	public static boolean isNumeric(String cadena) {
		try {
			Integer.parseInt(cadena);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	/***
	 * Convertir String a Blob
	 * 
	 * @param s
	 * @return
	 * @throws SQLException
	 */
	public Blob getImagen(String s, Connection connection) throws SQLException {

		String strContent = s;
		byte[] byteContent = strContent.getBytes();
		Blob blob = connection.createBlob();
		blob.setBytes(1, byteContent);

		return blob;
	}

	/***
	 * Convert Blob en String
	 * 
	 * @param o
	 * @return
	 * @throws SQLException
	 */
	public String getBlobinString(Object o) throws SQLException {

		if (o != null) {
			Blob blob = (Blob) o;
			byte[] bdata = blob.getBytes(1, (int) blob.length());
			return new String(bdata);
		} else {
			return null;
		}
	}

	/***
	 * 
	 * @param pfecha
	 * @param dias
	 * @return
	 */
	public static java.util.Date agregarDias(java.util.Date pfecha, int dias) {
		Calendar calendario = Calendar.getInstance();
		calendario.setTime(pfecha);
		calendario.add(Calendar.DATE, dias);
		return calendario.getTime();
	}

	public String getOperatingSystem() {
		String os = System.getProperty("os.name");
		return os;
	}

	public static UUID generateUuid() {
		return UUID.randomUUID();
	}

	public static boolean esEmail(String email) {
		try {
			Pattern pat = Pattern.compile("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,3})$");
			Matcher mat = pat.matcher(email.trim().toLowerCase());
			return mat.matches();
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Error General", ex);
		}
		return false;
	}

	public static String getParameterSystem(Connection connection, String parameter, UUID id_compania)
			throws Exception {

		String result = "";
		UtilSQL util = new UtilSQL();

		String query = "select valor_texto , valor_numerico \r\n" + "from \"general\".t_parametro_sistema tps \r\n"
				+ "where id_parametro_sistema = ?\r\n" + "and id_compania = ?\r\n"
				+ "and fhasta = to_date('31-12-2999','dd-mm-yyyy') ";

		List<Object> res_sql = util.ejecutarSQL(query, new Object[] { parameter, id_compania }, connection);

		LOGGER.info("res_sql..... " + res_sql.size());
		if (!res_sql.isEmpty()) {
			for (Object objeto : res_sql) {
				Object[] res = (Object[]) objeto;

				result = res[0] != null ? res[0].toString() : "";
			}
		}

		return result;
	}

	public static double roundD(double number, int digits) {

		return Precision.round(number, digits);
	}
}
