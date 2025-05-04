package cliente;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import mensajes.Mensaje;
import mensajes.MensajeTexto;
import mensajes.MensajeVacio;
import mensajes.TMensaje;

public class ThreadReceptor extends Thread {
	private int numThread;
	private String archivo;
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private FileOutputStream outputFile;
	private GestorEscritura escritura;
	private GestorFicheros ficheros;
	
	public ThreadReceptor(String nombre, String archivo, String ip, String port, GestorEscritura escritura, int numThread, GestorFicheros ficheros) throws IOException {
		this.numThread = numThread;
		this.archivo = archivo;
		this.socket = new Socket(ip, Integer.parseInt(port));
		this.output = new ObjectOutputStream(socket.getOutputStream());
		this.input = new ObjectInputStream(socket.getInputStream());
		this.outputFile = new FileOutputStream(Cliente.RUTA_CLIENTES + nombre + "/" + archivo);
		this.escritura = escritura;
		this.ficheros = ficheros;
		this.start();
	}
	
	@Override
	public void run() {
		//Queremos leer los datos desde el socket y escribirlos en el archivo
		try {
			Mensaje m = (MensajeVacio) input.readObject();
			if (m.getTipo() != TMensaje.M_INICIO_EMISION) {
				ClienteIOController.error("Error recibiendo el mensaje de inicio de emisión para el archivo " + archivo);
				desconectar();
				return;
			}
		} catch (ClassNotFoundException | IOException e) {
			ClienteIOController.error("Error al recibir el mensaje de inicio de emisión para el archivo " + archivo);
			desconectar();
			return;
		}
		
		byte[] buffer = new byte[1024];
		int bytesLeidos = 0;
		
		try {
			while ((bytesLeidos = input.read(buffer)) > 0) {
				outputFile.write(buffer, 0, bytesLeidos);
			}
		} catch (IOException e) {
			ClienteIOController.error("Error recibiendo parte del archivo " + archivo);
			desconectar();
			return;
		}
		
		try {
			ClienteIOController.log("Recibido el archivo " + archivo);
			Mensaje m = new MensajeVacio(TMensaje.M_DESCONECTAR);
			output.writeObject(m);
			output.flush();
		} catch (IOException e) {
			ClienteIOController.error("Error enviando el mensaje de desconexión (archivo " + archivo + ")");
		}
		
		try {
			if (!ficheros.existeFichero(archivo)) {
				ficheros.addFichero(archivo);
			}
		} catch (InterruptedException e) {
			ClienteIOController.error("Error añadiendo el archivo " + archivo);
		}
		
		try {
			Mensaje m = new MensajeTexto(TMensaje.M_FIN_EMISION, archivo);
			escritura.escribir(numThread, m);
		} catch (IOException e) {
			ClienteIOController.error("Error enviando el mensaje de fin de transmisión (archivo: " + archivo + ")");
		}
		desconectar();
	}
	
	private void desconectar() {
		try {
			socket.close();
			output.close();
			input.close();
			outputFile.close();
			
		} catch (IOException e) {
			ClienteIOController.error("Error cerrando el thread de recepción del archivo " + archivo);
		}
	}
}
