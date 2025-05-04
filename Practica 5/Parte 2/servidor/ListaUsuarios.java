package servidor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListaUsuarios implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Usuario> lista;
	
	public ListaUsuarios(Map<String, Usuario> mapa) {
		lista = new ArrayList<>();
		for (Usuario u : mapa.values()) {
			lista.add(u);
		}
	}
	
	public List<Usuario> getLista() {
		return lista;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" - Usuarios en el sistema - ");
		for (Usuario u : lista) {
			sb.append(u).append("\n");
			sb.append(" --------------- ");
		}
		return sb.toString();
	}
}
