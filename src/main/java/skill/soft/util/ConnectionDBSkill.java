package skill.soft.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


/***
 * CLASE QUE PERMITE LA CONEXION CON LA BASE DE DATOS
 * @author SMART
 *
 */
@Named
@RequestScoped 
public class ConnectionDBSkill {
	
	@Inject /**IDENTIFICA EL PUNTO DE INYECCION DEL BEAN**/
	private Propiedades propiedades;
	
	/**VARAIBLE QUE PERMITE OBTENER DATOS DEL PROPERTIES CORRESPONDIENTE PARA OBTENER DATOS DE CONEXION **/
	private DataSource datasource;
		
	/**VARIABLE QUE PERMITE REALIZAR EL MANEJO DE LOGS**/
	private static final Logger LOGGER = Logger.getLogger(ConnectionDBSkill.class.getName());
	
	/**VARIABLE QUE PERMITE REALIZAR LA CONEXION CON LA BD Y OBTENER INSTANCIAS STATEMENT**/
	private Connection connection;	
	
	/***
	 * METODO CONSTRUCTOR
	 */
	public ConnectionDBSkill() {
		super();
	}
	
	/***
	 * METODO DE INICIALIZACION DESPUES DEL CDI (INYECCION DE DEPENDENCIAS)
	 * @throws IOException
	 */
    @PostConstruct
	public void afterCreate() {
		InitialContext initial;
		try {
			LOGGER.info("1");
			initial = new InitialContext();
			datasource = (DataSource) initial.lookup(propiedades.getPropertiesDS("DSSKILL"));
			LOGGER.info("2");
		} catch (NamingException e ) {
			e.printStackTrace();
		}
	}
	
	/***
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException{
		connection = datasource.getConnection();
		return connection;
	}
	
	/***
	 * 
	 * @param connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
}
