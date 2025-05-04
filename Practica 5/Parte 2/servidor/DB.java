package servidor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import concurrencia.ReadWriteController;
import concurrencia.SemaforoRW;
import mensajes.Mensaje;
import mensajes.MensajeTexto;
import mensajes.TMensaje;

public class DB {
	private static String RUTA_DB = "./usuarios/database.txt";
	private static String RUTA_SERVIDOR = "./usuarios/";
	
	private Map<String, Usuario> usuarios;
	private ReadWriteController controlador;
	
	@SuppressWarnings("unchecked")
	public DB() {
		controlador = new SemaforoRW();
		FileInputStream inputFile = null;
		try {
			inputFile = new FileInputStream(RUTA_DB);
		} catch (FileNotFoundException e) {
			ServerIO.log("Base de datos no encontrada, creando una nueva");
			usuarios = new HashMap<>();
			return;
		}
		ObjectInputStream input;
		try {
			input = new ObjectInputStream(inputFile);
			this.usuarios = (Map<String, Usuario>) input.readObject();
		} catch (Exception e) {
			ServerIO.error("Error abriendo la base de datos");
		}
		
		try {
			inputFile.close();
		} catch (IOException e) {
			ServerIO.error("Error cerrando la base de datos");
		}
	}
	
	public void conectarUsuario(Usuario u) throws InterruptedException {
		controlador.request_write();
		String nombre = u.getId();
		usuarios.put(nombre, u);
		usuarios.get(nombre).conectar();
		controlador.release_write();
	}
	
	public void desconectarUsuario(String nombre) throws InterruptedException {
		controlador.request_write();
		usuarios.get(nombre).desconectar();
		controlador.release_write();
	}
	
	public void addFicheroUsuario(String u, String f) throws InterruptedException {
		controlador.request_write();
		usuarios.get(u).addFichero(f);
		controlador.release_write();
	}
	
	public void eliminarUsuario(String id) throws InterruptedException {
		controlador.request_write();
		usuarios.remove(id);
		controlador.release_write();
	}
	
	public void enviarUsuarios(String id, MapaEscritores me) throws InterruptedException {
		controlador.request_read();
		try {
			ListaUsuarios lu = new ListaUsuarios(usuarios);
			Mensaje m = new MensajeTexto(TMensaje.M_CONFIRMACION_LISTA_USUARIOS, lu.toString());
			me.escribir(id, m);
		} catch (Exception e) {
			ServerIO.error("Error enviando la lista de usuarios al cliente " + id);
		}
		controlador.release_read();
	}
	
	public String getPropietarioFichero(String fichero) throws InterruptedException {
		String nombre = null;
		controlador.request_read();
		for (Usuario u : usuarios.values()) {
			if (u.isConnected() && u.getFicheros().contains(fichero)) {
				nombre = u.getId();
				break;
			}
		}
		controlador.release_read();
		return nombre;
	}
	
	public void guardar() {
		FileOutputStream file = null;
		try {
			file = new FileOutputStream(RUTA_DB);
		} catch (FileNotFoundException e1) {
			File archivo = new File(RUTA_DB);
			if (!archivo.exists()) {
				try {
					File carpeta = new File(RUTA_SERVIDOR);
					carpeta.mkdir();
					archivo.createNewFile();
				} catch (IOException e2) {
					ServerIO.error("Error creando la base de datos");
					return;
				}
			}
			try {
				file = new FileOutputStream(RUTA_DB);
			} catch (FileNotFoundException e3) {
				ServerIO.error("Ha habido un error creando la base de datos");
			}
		}
		ObjectOutputStream output;
		try {
			output = new ObjectOutputStream(file);
			controlador.request_read();
			output.writeObject(usuarios);
			controlador.release_read();
		} catch (Exception e) {
			ServerIO.error("Error guardando los datos en la base de datos");
		}
		
		try {
			file.close();
		} catch (IOException e) {
			ServerIO.error("Error cerrando la base de datos");
		}
	}
}
