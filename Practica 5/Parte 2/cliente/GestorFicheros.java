package cliente;

import concurrencia.ReadWriteController;
import concurrencia.MonitorRW;
import java.util.List;

public class GestorFicheros {
	private ReadWriteController controlador;
	private List<String> ficheros;
	
	public GestorFicheros(List<String> ficheros) {
		this.controlador = new MonitorRW();
		this.ficheros = ficheros;
	}
	
	public boolean existeFichero(String f) throws InterruptedException {
		controlador.request_read();
		boolean ans = ficheros.contains(f);
		controlador.release_read();
		return ans;
	}
	
	public void addFichero(String f) throws InterruptedException {
		controlador.request_write();
		ficheros.add(f);
		controlador.release_write();
	}
}
