package skill.soft.models;

import java.util.List;

/***
 * Clase para envviar respuesta de logeo
 * @author vmacas
 *
 */
public class GenericUser {
	private Integer code;
	private String message;
	private String status;
	private String token;
	private List<LoggedUserData> detailUsers;
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public List<LoggedUserData> getDetailUsers() {
		return detailUsers;
	}
	public void setDetailUsers(List<LoggedUserData> detailUser) {
		this.detailUsers = detailUser;
	}
	
	public GenericUser() {
		super();
		// TODO Auto-generated constructor stub
	}
}
