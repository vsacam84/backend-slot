package skill.soft.bean;

import java.security.Principal;
import java.util.List;

public class User implements Principal {
	
	private String username;
	private List<String> roles;
	private String id_agente;

	public String getId_agente() {
		return id_agente;
	}

	public void setId_agente(String id_agente) {
		this.id_agente = id_agente;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getName() {
		return username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	

}

