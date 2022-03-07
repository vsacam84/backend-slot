package skill.soft.rest;

import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;

/**PERMITE LA DEFINICIOPN DE PATH PARA ACCESO A URL**/
/**POR DEFECTO ES /rest**/
@ApplicationPath("/api/")
public class RestApplication extends Application {
}