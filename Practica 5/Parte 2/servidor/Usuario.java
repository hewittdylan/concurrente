package servidor;

import java.io.Serializable;
import java.util.List;

public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private List<String> ficheros;
	private volatile boolean conectado;
	
	public Usuario(String id, List<String> ficheros) {
		this.id = id;
		this.ficheros = ficheros;
		this.conectado = false;
	}
	
	public String getId() {
		return id;
	}
	
	public List<String> getFicheros() {
		return ficheros;
	}
	
	public boolean isConnected() {
		return conectado;
	}
	
	public void conectar() {
		conectado = true;
	}
	
	public void desconectar() {
		conectado = false;
	}
	
	public boolean addFichero(String f) {
		return ficheros.add(f);
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Nombre del usuario: ").append(id);
		s.append("\nConectado: ");
		s.append(conectado ? "Si" : "No");
		s.append("\nFicheros de " + id + ": ");
		for (String f : ficheros) {
			s.append("\n").append(f);
		}
		return s.toString();
	}
}
