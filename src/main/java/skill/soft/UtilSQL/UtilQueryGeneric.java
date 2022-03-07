package skill.soft.UtilSQL;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

public class UtilQueryGeneric {

	static Logger log = Logger.getLogger(UtilQueryGeneric.class);

	/**
	 * Devuelve una lista de objetos con los resultados de la consulta ejemplo;
	 * getListObject(SQLrelPro, new Object[] {csolicitud}) ;
	 *
	 * @param consulta
	 * @param parametros
	 * @return
	 */
	public List<Object> getListObject(String consulta, Object[] parametros, Connection conn) {
		List<Object> lstValores = null;
		UtilSQL u = new UtilSQL();

		try {

			log.info("consulta " + consulta);
			log.info("parametros: " + parametros);
			lstValores = u.ejecutarSQL(consulta, parametros, conn);

		} catch (SQLException ex) {
			log.error("Error SQLException", ex);
		} catch (IOException ex) {
			log.error("Error IOException", ex);
		} catch (Exception ex) {
			log.error("Error Exception", ex);
		} finally {
			try {
				Util.cierreconexiones(conn);
			} catch (Exception ex) {
				log.error("Error SQLException", ex);
			}
		}

		return lstValores;
	}

	/**
	 * Metodo que consulta directamente a fit2 y obtiene una lista de string de
	 * resultados creando y cerrando la conexion en el proceso
	 * 
	 * @param consulta   String
	 * @param parametros Object[]
	 * @return
	 */
	public List<String> getListString(String consulta, Object[] parametros, Connection conn) {
		List<String> lstValores = null;
		UtilSQL u = new UtilSQL();

		try {

			log.info("consulta fit2 " + consulta);
			log.info("parametros: " + parametros);
			lstValores = u.baseConsultas(consulta, parametros, conn);

		} catch (SQLException ex) {
			log.error("Error SQLException", ex);
		} catch (IOException ex) {
			log.error("Error IOException", ex);
		} catch (Exception ex) {
			log.error("Error Exception", ex);
		} finally {
			try {
				Util.cierreconexiones(conn);
			} catch (Exception ex) {
				log.error("Error SQLException", ex);
			}
		}

		return lstValores;
	}

	/**
	 * metodo para ejecutar las sentencias en cascada con un solo commit al final
	 * 
	 * @param lsQuerys
	 * @param lsParametros
	 * @param connGD
	 * @param connFit2
	 * @autor fpenaloza
	 * @return si es >0 es que si hizo los inserts
	 */
	public String ejecutaSentenciasCascada(List<String> lsQuerys, List<Object[]> lsParametros, String conexion,
			Connection conn) {
		String resp = "";
		int rowsUpdated = 0;
		Connection connet = null;
		PreparedStatement preparedStatement = null;
		try {

			UtilSQL usql = new UtilSQL();
			connet = conn;
			log.debug("ejecuto conexion..");

			preparedStatement = null;
			for (int i = 0; i < lsQuerys.size(); i++) {
				if (i == 0 || (i > 0 && rowsUpdated > 0)) {
					preparedStatement = connet.prepareStatement(lsQuerys.get(i));
					preparedStatement = usql.preparaDatos(lsParametros.get(i), preparedStatement);
					rowsUpdated = preparedStatement.executeUpdate();
				} else { // cuando detecta que no inserto nada en el proceso
							// termina la ejecucion
					break;
				}
			}
			usql.cerrarPreparedStatement(preparedStatement);
			if (rowsUpdated > 0) {
				connet.commit();
				resp = String.valueOf(rowsUpdated);
			} else {
				log.error("no se hizo commit");
			}
		} catch (SQLException ex) {
			log.error("Error SQLException", ex);
			resp = "error " + ex.toString();
		} catch (IOException ex) {
			log.error("Error IOException", ex);
			resp = "error " + ex.toString();
		} catch (Exception ex) {
			log.error("Error Exception", ex);
			resp = "error " + ex.toString();
		} finally {

			try {
				preparedStatement.close();
				if (connet != null && !connet.isClosed()) {
					connet.close();
				}
				if (conn != null && !connet.isClosed()) {
					conn.close();
				}
			} catch (SQLException ex) {
				log.error(ex);

			}
		}
		return resp;
	}
}
