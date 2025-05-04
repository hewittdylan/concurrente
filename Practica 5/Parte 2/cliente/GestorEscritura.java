package cliente;

import concurrencia.LockBakery;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class GestorEscritura {
	private LockBakery controlador;
	private ObjectOutputStream escritor;
	
	public GestorEscritura(ObjectOutputStream obj) {
		this.escritor = obj;
		this.controlador = new LockBakery();
	}
	
	public void escribir(int id, Object obj) throws IOException {
		controlador.takeLock(id);
		escritor.writeObject(obj);
		escritor.flush();
		controlador.releaseLock(id);
	}
	
	public void cerrar(int id) throws IOException {
		controlador.takeLock(id);
		escritor.close();
		controlador.releaseLock(id);
	}
}
