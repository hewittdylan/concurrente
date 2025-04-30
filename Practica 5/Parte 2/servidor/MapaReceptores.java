package servidor;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

import concurrencia.ReadWriteController;
import concurrencia.SemaforoRW;

public class MapaReceptores {
	//Guardamos para cada fichero los receptores que quieren acceder
	private Map<String, Queue<String>> solicitudesPorFichero;
	private ReadWriteController control;
	
	public MapaReceptores() {
		this.solicitudesPorFichero = new HashMap<>();
		this.control = new SemaforoRW();
	}
	
	public void addReceptor(String fichero, String receptor) throws InterruptedException {
		control.request_write();
		if (solicitudesPorFichero.get(fichero) == null) {  //Primera petición para el fichero
			solicitudesPorFichero.put(fichero, new ArrayDeque<String>());
		}
		solicitudesPorFichero.get(fichero).add(receptor);
		control.release_write();
	}
	
	public String getReceptor(String fichero) throws InterruptedException {
		String ans = null;
		control.request_write();
		try {
			ans = solicitudesPorFichero.get(fichero).remove();
		} catch (NoSuchElementException e) {  //Si la cola se ha quedado vacia eliminamos la entrada del maap
			solicitudesPorFichero.remove(fichero);
		}
		control.release_write();
		return ans;
	}
}
