package servidor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import concurrencia.MonitorRW;
import concurrencia.ReadWriteController;

public class MapaEscritores {
	private ReadWriteController controlador;
	private Map<String, ObjectOutputStream> datosEscribir;
	
	public MapaEscritores() {
		controlador = new MonitorRW();
		datosEscribir = new HashMap<>();
	}
	
	public void addThread(String id, ObjectOutputStream o) throws InterruptedException {
		controlador.request_write();
		datosEscribir.put(id, o);
		controlador.release_write();
	}
	
	public void escribir(String id, Object obj) throws InterruptedException {
		controlador.request_write();
		try {
			datosEscribir.get(id).writeObject(obj);
			datosEscribir.get(id).flush();
		} catch (IOException e) {
			ServerIO.error("Error escribiendo un mensaje en el canal del cliente " + id);
		}
		controlador.release_write();
	}
	
	public void cerrar(String id) throws InterruptedException, IOException {
		controlador.request_write();
		datosEscribir.get(id).close();
		controlador.release_write();
	}
}
