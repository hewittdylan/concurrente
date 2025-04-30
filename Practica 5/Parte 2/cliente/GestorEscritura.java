package cliente;

import concurrencia.LockTicket;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class GestorEscritura {
	private LockTicket controlador;
	private ObjectOutputStream escritor;
	
	public GestorEscritura(ObjectOutputStream obj) {
		this.escritor = obj;
		this.controlador = new LockTicket();
	}
	
	public void escribir(int id, Object obj) throws IOException {
		controlador.takeLock();
		escritor.writeObject(obj);
		escritor.flush();
		controlador.releaseLock();
	}
	
	public void cerrar(int id) throws IOException {
		controlador.takeLock();
		escritor.close();
		controlador.releaseLock();
	}
}
