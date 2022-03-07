package skill.soft.general;

/***
 * Clase para consulta a base de datos
 * @author vmacas
 *
 */
public class SQLQuery {
	
	public static final String validateUser = "select count(0) from persona.t_usuario tu \r\n" + 
			"where id_usuario = ? and fhasta = to_date('31-12-2999','dd-mm-yyyy')";
	
	public static final String insertUser = "INSERT INTO persona.t_usuario (id_usuario,nombre,contrasenia,fingreso,fdesde,fhasta,usuario_creacion,fecha_creacion,cambio_contrasenia,estado) VALUES\r\n" + 
			"	 (?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,to_date('31-12-2999','dd-mm-yyyy'),'ADMIN',CURRENT_TIMESTAMP,'0','ACT')";

	public static final String LoginSelectSlot = "select\r\n" + 
			"	tu.id_usuario,\r\n" + 
			"	tu.nombre,\r\n" + 
			"	tu.contrasenia,\r\n" + 
			"	'Juego Tragamonedas' as rol_principal\r\n" + 
			"from\r\n" + 
			"	persona.t_usuario tu\r\n" + 
			"where\r\n" + 
			"	tu.id_usuario = ?\r\n" + 
			"	and tu.fhasta = TO_DATE('2999-12-31', 'YYYY-MM-DD')\r\n" + 
			"	and tu.estado = 'ACT'\r\n" + 
			"group by\r\n" + 
			"	tu.id_usuario,\r\n" + 
			"	tu.nombre,\r\n" + 
			"	tu.contrasenia,\r\n" + 
			"	tu.cambio_contrasenia";
	
	public static final String Insert_Log_Login = "INSERT INTO persona.log_ingreso(\r\n"
			+ "	id_usuario, fecha, hora, ip, fdesde, fhasta, usuario_creacion, fecha_creacion)\r\n"
			+ "	VALUES (?, CURRENT_TIMESTAMP, (SELECT TO_CHAR(CURRENT_TIMESTAMP,'HH24') || ':' || TO_CHAR(CURRENT_TIMESTAMP,'MI')  || ':' || TO_CHAR(CURRENT_TIMESTAMP,'SS')), ?, CURRENT_TIMESTAMP, to_date('31-12-2999','dd-mm-yyyy'), ?, CURRENT_TIMESTAMP)";

}
