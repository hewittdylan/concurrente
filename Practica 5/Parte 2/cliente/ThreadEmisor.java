package cliente;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import mensajes.Mensaje;
import mensajes.MensajeVacio;
import mensajes.TMensaje;

public class ThreadEmisor extends Thread {
	private Socket socket;
	private FileInputStream file;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private String archivo;
	
	public ThreadEmisor(String archivo, Socket socket) {
		this.archivo = archivo;
		this.socket = socket;
		this.start();
	}
	
	@Override
	public void run() {
		try {
			input = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			ClienteIOController.error("Error abriendo los canales para enviar el archivo " + archivo);
			desconectar();
			return;
		}
		
		try {
			file = new FileInputStream(Cliente.RUTA_CLIENTES + archivo);
		} catch (FileNotFoundException e) {
			ClienteIOController.error("Error abriendo el archivo");
			desconectar();
			return;
		}
		
		try {
			output.writeObject(new MensajeVacio(TMensaje.M_INICIO_EMISION));
			output.flush();
		} catch (IOException e) {
			ClienteIOController.error("Error enviando el mensaje de inicio de emisión del archivo " + archivo);
			desconectar();
			return;
		}
		
		byte[] buffer = new byte[8192];  //Tamaño máximo de un paquete a enviar via network
		int bytesLeidos = 0;
		
		try {
			while ((bytesLeidos = file.read(buffer)) > 0) {  //Mientras siga leyendo del archivo
				output.write(buffer, 0, bytesLeidos);
				output.flush();
			}
			//Cerramos el socket para evitar que el receptor se quede colgado
			socket.shutdownOutput();
			file.close();
		} catch (IOException e) {
			ClienteIOController.error("Error enviando un tramo del archivo " + archivo);
			desconectar();
			return;
		}
		ClienteIOController.log("Enviando el archivo " + archivo);
		Mensaje m;
		try {
			//Mensaje de fin de cierre emisión
			m = (MensajeVacio) input.readObject();
		} catch (ClassNotFoundException | IOException e) {
			ClienteIOController.error("Error recibiendo mensaje de desconexión desde el receptor del archivo " + archivo);
			desconectar();
			return;
		}
		if (m.getTipo() != TMensaje.M_DESCONECTAR) {
			ClienteIOController.error("No se ha recibido un mensaje de desconexión por parte del receptor");
		}
		try {
			socket.close();
			input.close();
		} catch (IOException e) {
			ClienteIOController.error("Error cerrando el thread de emisión del archivo " + archivo);
		}
	}
	
	private void desconectar() {
		try {
			socket.close();
			file.close();
			input.close();
			output.close();
			
		} catch (IOException e) {
			ClienteIOController.error("Error cerrando el thread de emisión del archivo " + archivo);
		}
	}
}
