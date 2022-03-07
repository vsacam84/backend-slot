package skill.soft.general;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/***
 * CLASE QUE PERMITE LEER UN ARCHIVO 
 * PROPERTIES Y ACCEDER A SU CONTENIDO
 * @author COOPCREA
 *
 */

/**CDI QUE COMPARTE EL ESTADO A TRAVES DE TODAS LAS INTERACCIOBES DE LOS CLIENTES**/
@ApplicationScoped
public class Propiedades1 implements Serializable{

	/**SERIAL DE CLASE**/
	private static final long serialVersionUID = 1L;
	
	/**VARIABLE QUE PERMITE LEER UN  FICHERO Y ACCEDER A SU CONTENIDO**/
	private Properties properties;
	
	/**VARIABLE QUE PERMITE REALIZAR EL MANEJO DE LOGS**/
	private final static Logger LOGGER = Logger.getLogger(Propiedades1.class.getName());
	
	/***
	 *  EJECUTA DESPUES DE QUE SE COMPLETE LA INYECCION DE DEPENDENCIA.
	 *  -SE UTILIZA PARA VALIDAR LAS PROPIEDADES DE BEAN O PARA INICIALIZAR CUALQUIER TAREA.
	 */
	@PostConstruct
	public void init() {
		properties = new Properties();
	}
	
	
	public String getPropertiesMail(String data) {
		try {
			InputStream archivo = Propiedades1.class.getClassLoader().getResourceAsStream("mail.properties");
			properties.load(archivo);
		} catch (FileNotFoundException e) {
			LOGGER.info("NO SE ENCUENTRA EL ARCHIVO");
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.info("NO SE PUEDE LEER EL ARCHIVO");
			e.printStackTrace();
		}
		return properties.getProperty(data);
	}
	
}
