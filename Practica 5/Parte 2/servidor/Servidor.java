package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Servidor {
	public static final int NUM_PUERTO = 2000;
	public static final String EXIT_KEY = "salir";
	private static ServerSocket sSocket;
	private volatile static boolean abierto = true;  //Mientras esté abierto acepta conexiones
	
	private List<OyenteCliente> clientes;
	private DB bd;
	private MapaEscritores escritores;
	private MapaReceptores receptores;
	private int puertoClienteNuevo;
	
	public Servidor() throws IOException {
		sSocket = new ServerSocket(NUM_PUERTO);
		clientes = new ArrayList<>();
		bd = new DB();
		escritores = new MapaEscritores();
		receptores = new MapaReceptores();
		puertoClienteNuevo = NUM_PUERTO + 1;
	}
	
	public void start() {
		CloseThread ct = new CloseThread();
		ct.start();
		ServerIO.log("Servidor iniciado y listo para atender clientes");
		
		while (abierto) {
			try {
				Socket socket = sSocket.accept();
				OyenteCliente oc = new OyenteCliente(socket, bd, escritores, receptores, puertoClienteNuevo);
				oc.start();
				clientes.add(oc);
				puertoClienteNuevo++;
			} catch (IOException e) {
				ServerIO.log("Error en la conexión");
			}
		}
		cerrar(ct);
	}
	
	public void cerrar(CloseThread ct) {
		try {
			ct.join();
		} catch (InterruptedException e) {
			ServerIO.error("Error cerrando el thread de cierre del servidor");
		}
		for (OyenteCliente oc : clientes) {
			try {
				oc.join();
				ServerIO.log("Cerrada la conexión con el cliente " + oc.getID() + " correctamente");
			} catch (InterruptedException e) {
				ServerIO.error("Fallo cerrando las conexiones de los clientes");
			}
		}
		ServerIO.log("Cerradas todas las conexiones de los clientes. Guardamos la base de datos");
		bd.guardar();
		ServerIO.log("Datos guardados correctamente, cerramos el servidor");
	}
	
	private static class CloseThread extends Thread {
		@Override
		public void run() {
			Scanner scanner = new Scanner(System.in);
			while (abierto) {
				System.out.print("Escibe 'salir' para terminar: ");
				String input = scanner.nextLine();
				if (input.equals(EXIT_KEY)) {
					ServerIO.log("Se ha cerrado el servidor, no atenderá nuevos clientes");
					abierto = false;
					try {
						sSocket.close();
					} catch (IOException e) {
						ServerIO.error("Error cerrando el socket del servidor");
					}
					scanner.close();
				}
			}
		}
	}
}
