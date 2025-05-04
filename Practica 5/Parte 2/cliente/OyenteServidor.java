package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import mensajes.Mensaje;
import mensajes.MensajeTexto;
import mensajes.TMensaje;

public class OyenteServidor extends Thread {
	private static final int NUM_HILO = 1;
	
	private String nombre;
	private ServerSocket sSocket;
	private ObjectInputStream input;
	private int port;
	private GestorEscritura escritura;
	private GestorFicheros ficheros;
	private List<Thread> emisores;
	private int hilosReceptor;
	
	public OyenteServidor(String nombre, Socket cs, GestorEscritura ge, GestorFicheros gf) throws IOException, ClassNotFoundException {
		this.nombre = nombre;
		this.escritura = ge;
		this.ficheros = gf;
		this.emisores = new ArrayList<>();
		
		this.hilosReceptor = NUM_HILO + 1;
		this.input = new ObjectInputStream(cs.getInputStream());
		Mensaje m = (Mensaje) input.readObject();
		if (m.getTipo() != TMensaje.M_CONFIRMACION_CONEXION) {
			ClienteIOController.error("No se ha recibido la confirmación de conexión para el cliente: " + nombre);
			throw new RuntimeException();
		}
		this.port = Integer.parseInt(((MensajeTexto) m).getContenido());
		ClienteIOController.log("Creando Server Socket con puerto " + port);
		this.sSocket = new ServerSocket(port);
	}
	
	@Override
	public void run() {
		while (!isInterrupted()) {
			atenderPeticiones();
		}
		
		for (Thread t : emisores) {
			try {
				t.join();
			} catch (InterruptedException e) {
				ClienteIOController.error("Error cerrando uno de los hilos de transmisión de archivos para el cliente " + nombre);
			}
		}
	}
	
	private void atenderPeticiones() {
		Mensaje m = null;
		try {
			m = (Mensaje) input.readObject();
		} catch (ClassNotFoundException e) {
			ClienteIOController.error("Error al recibir un mensaje del servidor. Desconectando");
			interrupt();
		} catch (IOException e) {
			interrupt();
			return;
		}
		
		switch (m.getTipo()) {
		case M_CONFIRMACION_CONEXION:
			ClienteIOController.log("Conectado al cliente " + nombre);
			break;
		case M_CONFIRMACION_LISTA_USUARIOS:
			String info = ((MensajeTexto) m).getContenido();
			ClienteIOController.print("Información disponible en el sistema: \n" + info);
			break;
		case M_FICHERO_INEXISTENTE:
			ClienteIOController.warning("El fichero solicitado no existe");
			break;
		case M_PEDIR_FICHERO:  //Creamos otro hilo para el (P2P)
			String archivo = ((MensajeTexto) m).getContenido();
			String ip = "localhost";
			try {
				MensajeTexto mt = new MensajeTexto(TMensaje.M_PREPARADO_CS, archivo + "-" + ip + "-" + port);
				escritura.escribir(NUM_HILO, mt);
			} catch (IOException e) {
				ClienteIOController.error("Error enviando el mensaje preparado cliente-servidor para el archivo " + archivo);
				interrupt();
				return;
			}
			Socket emisor;
			try {
				emisor = sSocket.accept();
				ThreadEmisor te = new ThreadEmisor(nombre + "/" + archivo, emisor);
				emisores.add(te);
			} catch (IOException e) {
				ClienteIOController.error("Error conectando al receptor del archivo " + archivo);
			}
			break;
		case M_PREPARADO_SC:  //Recibe un string con tres palabras archivo, ip y puerto
			String contenido = ((MensajeTexto) m).getContenido();
			String[] contenidos = contenido.split("-");
			String archivo2 = contenidos[0];
			String ip2 = contenidos[1];
			String puerto = contenidos[2];
			try {
				ThreadReceptor tr = new ThreadReceptor(nombre, archivo2, ip2, puerto, escritura, hilosReceptor, ficheros);
				emisores.add(tr);
				hilosReceptor++;
			} catch (IOException e) {
				ClienteIOController.error("Error conectando con el emisor del archivo " + archivo2);
			}
			break;
		default:
			ClienteIOController.error("Mensaje del servidor desconocido. Desconectando");
			interrupt();
		}
	}
}
