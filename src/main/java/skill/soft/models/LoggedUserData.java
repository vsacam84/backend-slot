package skill.soft.models;

/***
 * Clase de usuario envio de informacion por wen service
 * @author vmacas
 *
 */
public class LoggedUserData {
	
	private String username;
	private String fullName;
	private String password;
	private Integer idUsuario;
	private String rol_principal;
	private String cambio_contrasenia;
	private String credito;
	
	
	public String getCredito() {
		return credito;
	}
	public void setCredito(String credito) {
		this.credito = credito;
	}
	public String getCambio_contrasenia() {
		return cambio_contrasenia;
	}
	public void setCambio_contrasenia(String cambio_contrasenia) {
		this.cambio_contrasenia = cambio_contrasenia;
	}
	public String getRol_principal() {
		return rol_principal;
	}
	public void setRol_principal(String rol_principal) {
		this.rol_principal = rol_principal;
	}
	public LoggedUserData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	
}
