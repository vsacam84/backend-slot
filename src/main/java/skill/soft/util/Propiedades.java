package skill.soft.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
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
public class Propiedades implements Serializable{

	/**SERIAL DE CLASE**/
	private static final long serialVersionUID = 1L;
	
	/**VARIABLE QUE PERMITE LEER UN  FICHERO Y ACCEDER A SU CONTENIDO**/
	private Properties properties;
	
	/**VARIABLE QUE PERMITE REALIZAR EL MANEJO DE LOGS**/
	private final static Logger LOGGER = Logger.getLogger(Propiedades.class.getName());
	
	/***
	 *  ANOTACION DEFINE UN METODO COMO METODO DE INICIALIZACION DE UN BEAN DE RESORTE QUE SE 
	 *  EJECUTA DESPUES DE QUE SE COMPLETE LA INYECCION DE DEPENDENCIA.
	 *  -SE UTILIZA PARA VALIDAR LAS PROPIEDADES DE BEAN O PARA INICIALIZAR CUALQUIER TAREA.
	 */
	@PostConstruct
	public void init() {
		properties = new Properties();
	}
	
	/***
	* METODO QUE PERMITE OBTENER DATOS DE UN PROPERTIES
	* PARA CONEXIONES A LA BASE DE DATOS
	* @param data VALOR DE FICHERO AL QUE SE QUIERE ACCEDER
	* @return RETURN PARAMETRO REQUERIDO QUE SE ENCUENTRA EN EL PROPERTIES
	*/
	public String getPropertiesDS(String data) {
		
		try {
			InputStream archivo = Propiedades.class.getClassLoader().getResourceAsStream("ds.properties");
			properties.load(archivo);
		} catch (FileNotFoundException e) {
			LOGGER.info("NO SE ENCUENTRA EL ARCHIVO");
			e.printStackTrace();
		}catch(IOException e) {
			LOGGER.info("NO SE PUEDE LEER EL ARCHIVO");
			e.printStackTrace();
		}
		return properties.getProperty(data);
	}
	
	/***
	 * METODO QUE OBTIENE KEY STORAGE PARA DECRYPTION DE CLAVE CIFRADA
	 * @param data
	 * @return
	 */
	public String getPropertiesKeyStorage(String data) {
		try {
			InputStream archivo = Propiedades.class.getClassLoader().getResourceAsStream("security.properties");
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
	
	/***
	* METODO QUE PERMITE OBTENER DATOS DE UN PROPERTIES
	* @param data VALOR DE FICHERO AL QUE SE QUIERE ACCEDER
	* @return RETURN PARAMETRO REQUERIDO QUE SE ENCUENTRA EN EL PROPERTIES
	*/
	public String getPropertiesSimuladores(String data) {
		
		try {
			InputStream archivo = Propiedades.class.getClassLoader().getResourceAsStream("simuladores.properties");
			properties.load(archivo);
		} catch (FileNotFoundException e) {
			LOGGER.info("NO SE ENCUENTRA EL ARCHIVO");
			e.printStackTrace();
		}catch(IOException e) {
			LOGGER.info("NO SE PUEDE LEER EL ARCHIVO");
			e.printStackTrace();
		}
		return properties.getProperty(data);
	}
	
	/**
	 * Metodo para recuperar propiedades de un archivo .properties
	 * @param string
	 * @param file
	 * @return
	 */
	public String getPropiedad(String data, String file) {
		
		InputStream archivo = null;
		try {
			archivo = Propiedades.class.getClassLoader().getResourceAsStream(file.concat(".properties"));
			properties.load(archivo);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, String.format("No es posible encontrar la propiedad %s del archivo %s.properties: %s %s", data, file, e.getMessage(), e.getLocalizedMessage()));
		} finally {
			try {
				if(archivo != null) {
					archivo.close();
				}
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, String.format("No es posible cerrar el archivo properties: %s %s", e.getMessage(), e.getLocalizedMessage()));
			}
		}
		return properties.getProperty(data).trim();
	}
	
	public String getPropiedadMENSAJERIA(String data) throws IOException {

		InputStream archivo = Propiedades.class.getClassLoader().getResourceAsStream("mailContratos.properties");
		properties.load(archivo);
		return properties.getProperty(data);
	}
	
}
