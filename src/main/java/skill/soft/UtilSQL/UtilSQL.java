package skill.soft.UtilSQL;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ListIterator;

/***
 * CLASE GENERICA QUE PERMITE EJECUTAR SENTENCIAS SQL
 * 
 * @author COOPCREA
 *
 */
public class UtilSQL {

	/** VARIABLE QUE PERMITE REALIZAR EL MANEJO DE LOGS **/
	private final static Logger LOGGER = Logger.getLogger(UtilSQL.class.getName());

	public UtilSQL() {
		super();
	}

	/***
	 * METODO QUE EJECUTA CONSULTAS SQL Y RETORNA EL/LOS RESULTADO/S
	 * 
	 * @param consulta   PARAMETRO QUE RECIBE LA CONSULTA SQL
	 * @param argumentos NUMERO DE ARGUMENTOS QUE RECIBE LA CONSULTA SQL
	 * @param connection TIPO DE CONEXION A LA BD
	 * @return
	 * @throws Exception
	 */
	public List<Object> ejecutarSQL(String consulta, Object[] argumentos, Connection connection) throws Exception {
		List<Object> auxResultados = new ArrayList<Object>();
		ResultSet resultados = null;
		PreparedStatement preparedStatement = null;

		try {
			if (connection != null) {
				preparedStatement = connection.prepareStatement(consulta);
				// System.out.println("CONSULTA-------> "+consulta);
				preparedStatement = preparaDatos(argumentos, preparedStatement);
				resultados = preparedStatement.executeQuery();
				if (resultados != null) {
					ResultSetMetaData metadata = null;
					int numeroColumnas = 0;
					// int i = 0; // PARA PRUEBAS DE FUNCIONALIDAD DE SENTENCIAS SQL
					while (resultados.next()) {
						// System.out.println("LINEA " + i); //PARA PRUEBAS
						if (metadata == null) {
							metadata = resultados.getMetaData();
							numeroColumnas = metadata.getColumnCount();
						}
						Object[] fila = new Object[numeroColumnas];
						if (numeroColumnas > 1) {
							for (int j = 0; j < numeroColumnas; j++) {
								// System.out.println("\t" + i + "," + j + " " + resultados.getObject(j + 1));
								// //PARA PRUEBAS
								fila[j] = resultados.getObject(j + 1);
							}
							auxResultados.add(fila);
							// i += 1; // PRUEBAS
						} else {
							auxResultados.add(resultados.getObject(1));
							// i += 1; // PRUEBAS
						}
					} // FIN DEL WHILE QUE RECUPERA LOS DATOS DEL RESULTSET
				} // FIN IF DEL RESULTSET
			} // FIN IF QUE EVALUA LA CONEXION A BD
		} catch (SQLException e) {
			LOGGER.info("FALLO PREPAREDSTATEMENT");
			e.printStackTrace();
		} finally {
			LOGGER.info("SE REALIZA CIERRE DE JDBC");
			Util.cierreResultSet(resultados);
			Util.cierrePreparedStatement(preparedStatement);
		} // FIN DEL FINALLY
		return auxResultados;
	}// FIN DE LA CLASE

	/***
	 * METODO QUE DEVUELVE EL TIPO DE DATO OBTENIDO EN LA CONSULTA SQL
	 * 
	 * @param pObject            OBJETO QUE RECIBE LA CONSULTA SQL
	 * @param pPreparedStatement
	 * @return RETORNA UN PREPAREDSTATEMENT
	 * @throws Exception
	 */
	public PreparedStatement preparaDatos(Object[] pObject, PreparedStatement pPreparedStatement) throws Exception {

		if (pObject != null) {
			for (int i = 0; i < pObject.length; i++) {
				if ((pObject[i] instanceof String)) {
					pPreparedStatement.setString(i + 1, (String) pObject[i]);
				} else if ((pObject[i] instanceof Long)) {
					pPreparedStatement.setLong(i + 1, ((Long) pObject[i]).longValue());
				} else if ((pObject[i] instanceof Integer)) {
					pPreparedStatement.setInt(i + 1, ((Integer) pObject[i]).intValue());
				} else if ((pObject[i] instanceof BigDecimal)) {
					pPreparedStatement.setBigDecimal(i + 1, (BigDecimal) pObject[i]);
				} else if ((pObject[i] instanceof Float)) {
					pPreparedStatement.setFloat(i + 1, ((Float) pObject[i]).floatValue());
				} else if ((pObject[i] instanceof Double)) {
					pPreparedStatement.setDouble(i + 1, ((Double) pObject[i]).doubleValue());
				} else if ((pObject[i] instanceof Date)) {
					pPreparedStatement.setDate(i + 1, (Date) pObject[i]);
				} else if ((pObject[i] instanceof Timestamp)) {
					pPreparedStatement.setTimestamp(i + 1, (Timestamp) pObject[i]);
				} else if ((pObject[i] instanceof Blob)) {
					pPreparedStatement.setBlob(i + 1, (Blob) pObject[i]);
				} else if ((pObject[i] instanceof Clob)) {
					pPreparedStatement.setClob(i + 1, (Clob) pObject[i]);
				} else if ((pObject[i] instanceof UUID)) {
					pPreparedStatement.setObject(i + 1, (UUID) pObject[i]);
				} else if ((pObject[i] instanceof FileInputStream)) {
					FileInputStream fileInputStream = (FileInputStream) pObject[i];
					pPreparedStatement.setBinaryStream(i + 1, fileInputStream, fileInputStream.available());
				} else if (pObject[i] == null) {
					pPreparedStatement.setNull(i + 1, 12);
				}
			}
		}
		System.out.println("pPreparedStatement ******************************************* ");
		System.out.println(pPreparedStatement);
		return pPreparedStatement;
	}

	/***
	 * METODO QUE SE USA PARA EJECUTAR SENTENCIAS INSERT, UPDATE O DELETE
	 * 
	 * @param sentencia  RECIBE SENTENCIA SQL
	 * @param params     PARAMETROS QUE RECIBE LA SENTENCIA SQL
	 * @param connection CONEXION A LA BASE DE DATOS
	 * @return
	 * @throws Exception
	 */
	public int ejecutaSentencia(String sentencia, Object[] params, Connection connection) {
		System.out.println("PARAMS TO ejecutaSentencia---> " + params.length);
		PreparedStatement preparedStatement = null;
		int rowsUpdated = 0;
		try {
			preparedStatement = connection.prepareStatement(sentencia);
			preparedStatement = preparaDatos(params, preparedStatement);
			rowsUpdated = preparedStatement.executeUpdate();
			LOGGER.info("ROWS UPDATED ----> " + rowsUpdated);
			if (rowsUpdated <= 0) {
				LOGGER.info("ERROR AL ACTUALIZAR");
			}

		} catch (Exception e) {
			LOGGER.info("FALLO INSERT/UPDATE BD");
			LOGGER.info("DML CON ERROR: " + sentencia);
			e.printStackTrace();
		} finally {
			try {
				cerrarPreparedStatement(preparedStatement);
			} catch (Exception e) {
				LOGGER.info("FALLO AL CERRAR PREPAREDSTATEMENT");
				e.printStackTrace();
			}
		}
		return rowsUpdated;
	}

	/***
	 * METODO QUE PERMITE CERRAR LA EJECUCION DE SENTENCIAS INSERT, UPDATE O DELETE
	 * 
	 * @param pPreparedStatement
	 * @throws Exception
	 */
	public void cerrarPreparedStatement(PreparedStatement pPreparedStatement) throws Exception {
		if (pPreparedStatement != null)
			pPreparedStatement.close();
	}

	public List<String> baseConsultas(String query, Connection connection) {
		List<String> campos = new ArrayList<String>();
		campos.add("SELECCIONAR");
		ResultSet resultados = null;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			resultados = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = resultados.getMetaData();
			int colu = rsmd.getColumnCount();
			// int i1 = 0;
			while (resultados.next()) {
				for (int i = 1; i <= colu; i++) {
					campos.add(resultados.getString(i));
				}
			}
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Error SQLException " + ex);
			campos.add("error");
			return campos;
		} finally {
			try {
				Util.cierreResultSet(resultados);
				Util.cierrePreparedStatement(preparedStatement);
				Util.cierreconexiones(connection);
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE, "Error SQLException " + ex);
			}
		}
		return campos;
	}

	public List<String> baseConsultas(String query, Object[] argumentos, Connection connection) throws Exception {
		List<String> campos = new ArrayList<String>();
		ResultSet resultados = null;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement = preparaDatos(argumentos, preparedStatement);
			resultados = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = resultados.getMetaData();
			int colu = rsmd.getColumnCount();
			int i = 0;
			while (resultados.next()) {
				for (i = 1; i <= colu; i++) {
					campos.add(resultados.getString(i));
				}
			}
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Error SQLException " + ex);
			campos.add("error");
			return campos;
		} finally {
			try {
				Util.cierreResultSet(resultados);
				Util.cierrePreparedStatement(preparedStatement);
				Util.cierreconexiones(connection);
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE, "Error SQLException " + ex);
			}
		}
		return campos;
	}

	public String[] cambia(List<String> campos) {
		int tama = campos.size();
		int i = 0;
		String dat[] = new String[tama];
		ListIterator<String> iterator = campos.listIterator();
		while (iterator.hasNext()) {
			dat[i] = iterator.next();
			// System.out.println("dat[i]=" + dat[i]);
			i++;
		}
		return dat;
	}

	public List<String> baseConsultasNocierreOjo(String query, Object[] argumentos, Connection connection)
			throws Exception {
		List<String> campos = new ArrayList<String>();
		ResultSet resultados = null;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement = preparaDatos(argumentos, preparedStatement);
			resultados = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = resultados.getMetaData();
			int colu = rsmd.getColumnCount();
			int i = 0;
			while (resultados.next()) {
				for (i = 1; i <= colu; i++) {
					campos.add(resultados.getString(i));
				}
			}
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Error SQLException " + ex);
			campos.add("error");
			return campos;
		}
		return campos;
	}
}
