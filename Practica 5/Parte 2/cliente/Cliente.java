package cliente;

import servidor.Servidor;
import servidor.Usuario;
import java.net.Socket;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import mensajes.Mensaje;
import mensajes.MensajeTexto;
import mensajes.MensajeUsuario;
import mensajes.MensajeVacio;
import mensajes.TMensaje;

import java.util.ArrayList;

public class Cliente {
	protected static final String RUTA_CLIENTES = "./usuarios/";
	private static final int NUM_HILO = 0;
	
	private String nombreUsuario;
	private GestorEscritura escritura;
	private OyenteServidor oyenteServidor;
	private Usuario usuario;
	private GestorFicheros ficheros;
	private Socket socket;
	
	public Cliente(String nombre) throws IOException, ClassNotFoundException {
		this.nombreUsuario = nombre;
		List<String> ficherosUsuario = fetchFicheros();
		this.usuario = new Usuario(nombre, ficherosUsuario);
		this.ficheros = new GestorFicheros(ficherosUsuario);
		
		this.socket = new Socket("localhost", Servidor.NUM_PUERTO);
		
		ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
		Mensaje m = new MensajeUsuario(TMensaje.M_CONEXION, usuario);
		output.writeObject(m);
		output.flush();
		
		this.escritura = new GestorEscritura(output);
		this.oyenteServidor = new OyenteServidor(nombre, socket, escritura, ficheros);
		oyenteServidor.start();
	}
	
	private List<String> fetchFicheros() {
		File ruta = new File(RUTA_CLIENTES + nombreUsuario);
		if (!ruta.exists() || !ruta.isDirectory()) {  //Puede que la ruta exista, pero no sea una carpeta!!
			ruta.mkdirs();
			return new ArrayList<>();
		}
		List<String> ans = new ArrayList<>();
		File[] files = ruta.listFiles();
		for (File f : files) {
			ans.add(f.getName());
		}
		return ans;
	}
	
	public void consultaInfo() throws IOException {
		Mensaje m = new MensajeVacio(TMensaje.M_LISTA_USUARIOS);
		escritura.escribir(NUM_HILO, m);
	}
	
	public void descargarInfo(String archivo) throws IOException {
		try {
			if (ficheros.existeFichero(archivo)) {  //El usuario ya tiene el archivo, no necesita descargarlo
				ClienteIOController.warning("El cliente ya tiene el archivo, no necesita descargarlo");
				return;
			}
		} catch (InterruptedException e) {
			ClienteIOController.error("Error comprobando si el cliente tiene el archivo");
			return;
		}
		Mensaje m = new MensajeTexto(TMensaje.M_PEDIR_FICHERO, archivo);
		escritura.escribir(NUM_HILO, m);
	}
	
	public void desconectar() throws IOException, InterruptedException {
		Mensaje m = new MensajeVacio(TMensaje.M_DESCONECTAR);
		escritura.escribir(NUM_HILO, m);
		escritura.cerrar(NUM_HILO);
		oyenteServidor.join();
	}
	
	public String getNombre() {
		return nombreUsuario;
	}
}
