package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import mensajes.Mensaje;
import mensajes.MensajeTexto;
import mensajes.MensajeUsuario;
import mensajes.TMensaje;

public class OyenteCliente extends Thread {
	private volatile boolean conectado;
	private DB bd;
	private MapaEscritores escritores;
	private MapaReceptores receptores;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private String id;
	private int puertoClienteNuevo;
	
	public OyenteCliente(Socket s, DB bd, MapaEscritores escritores, MapaReceptores receptores, int puertoClienteNuevo) {
		this.conectado = true;
		this.bd = bd;
		this.escritores = escritores;
		this.receptores = receptores;
		this.puertoClienteNuevo = puertoClienteNuevo;
		try {
			input = new ObjectInputStream(s.getInputStream());
			output = new ObjectOutputStream(s.getOutputStream());
		} catch (Exception e) {
			ServerIO.error("Error obteniendo los canales de transmisión del cliente. Conexión abortada");
			interrupt();
		}
	}
	
	public String getID() {
		return id;
	}
	
	@Override
	public void run() {
		ServerIO.log("Iniciando conexión con nuevo cliente");
		conectar();
		if (conectado) {
			ServerIO.log("Iniciando escucha activa del canal del cliente " + id);
			iniciarEscucha();
		}
		
	}
	
	private void conectar() {
		MensajeUsuario mu = null;
		try {
			mu = (MensajeUsuario) input.readObject();
		} catch (ClassNotFoundException | IOException e) {
			ServerIO.error("Error en el mensaje de conexión de " + id);
		}	
		
		if (mu.getTipo() != TMensaje.M_CONEXION) {
			ServerIO.error("Error conectado con cliente. No se ha recibido mensaje de conexión");
			conectado = false;
			return;
		}
		
		Usuario usuario = mu.getContenido();
		this.id = usuario.getId();
		ServerIO.log("Solicitud de conexión de " + id);
		ServerIO.log("Enviando confirmación de conexión");
		try {
			Mensaje m = new MensajeTexto(TMensaje.M_CONFIRMACION_CONEXION, String.valueOf(puertoClienteNuevo));
			output.writeObject(m);
		} catch (IOException e) {
			ServerIO.error("Error enviando el mensaje de confirmación a " + id);
			conectado = false;
			return;
		}
		
		//Actualizamos los mapas
		try {
			this.escritores.addThread(id, output);
		} catch (InterruptedException e) {
			ServerIO.error("Error añadiendo el nuevo flujo de salida para el cliente " + id);
			conectado = false;
			return;
		}
		
		try {
			this.bd.conectarUsuario(usuario);
		} catch (InterruptedException e) {
			ServerIO.error("Error conectado al usuario " + id);
			conectado = false;
			return;
		}
		ServerIO.log("Conexión con " + id + " establecida correctamente");
	}
	
	private void iniciarEscucha() {
		Mensaje m;
		while (conectado) {
			try {
				m = (Mensaje)input.readObject();
			} catch (ClassNotFoundException | IOException e) {
				ServerIO.error("Error leyendo mensaje del cliente " + id + ". Desconectando");
				conectado = false;
				try {
					bd.desconectarUsuario(id);
				} catch (InterruptedException e1) {
					ServerIO.error("Error desconectando al cliente " + id);
				}
				return;
			}
			
			switch(m.getTipo()) {
			case M_LISTA_USUARIOS:
				ServerIO.log(id + "ha solicitado la lista de usuarios");
				try {
					bd.enviarUsuarios(id, escritores);
					ServerIO.log("Enviando la lista de usuarios a " + id);
				} catch (InterruptedException e) {
					ServerIO.error("Error enviando la lista de usuarios a " + id + ". Desconectando servidor");
					conectado = false;
				}
				break;
			case M_PEDIR_FICHERO:
				String f = ((MensajeTexto) m).getContenido();
				ServerIO.log("Al cliente " + id + " le han solicitado el archivo " + f);
				try {
					pedirFichero(f);
				} catch (InterruptedException e) {
					ServerIO.error("Error solicitando el fichero " + f + " al propietario. Desconectando");
					conectado = false;
				}
				break;
			case M_PREPARADO_CS:
				String f1 = ((MensajeTexto) m).getContenido();
				try {
					enviarPreparadoSC(f1);
				} catch (InterruptedException e) {
					ServerIO.error("Error enviando mensaje de preparado al cliente receptor. Desconectando");
					conectado = false;
				}
				break;
			case M_FIN_EMISION:
				String f2 = ((MensajeTexto) m).getContenido();
				ServerIO.log("El cliente " + id + " ha decargado correctamente el fichero " + f2 + " . Actualizando BD");
				try {
					bd.addFicheroUsuario(id, f2);
				} catch (InterruptedException e) {
					ServerIO.error("Error añadiendo el fichero " + f2 + " al cliente " + id + ". Desconectando");
					conectado = false;
				}
				break;
			case M_DESCONECTAR:
				ServerIO.log("El cliente " + id + " se ha desconectado correctamente");
				conectado = false;
				break;
			default:
				ServerIO.error("Recibido un mensaje desconocido (" + id + ")");
				conectado = false;
			}
		}
		
		try {
			bd.desconectarUsuario(id);
			escritores.cerrar(id);
		} catch (InterruptedException | IOException e) {
			ServerIO.error("Error desconectando al cliente " + id);
		}
	}
	
	private void pedirFichero(String fichero) throws InterruptedException {
		String emisor = bd.getPropietarioFichero(fichero);
		if (emisor == null) {
			ServerIO.error("El fichero solicitado no existe. Enviando respuesta al receptor");
			Mensaje m = new MensajeTexto(TMensaje.M_FICHERO_INEXISTENTE, fichero);
			escritores.escribir(id, m);
			return;
		}
		ServerIO.log("El usuario " + emisor + " tiene el archivo " + fichero + ". Enviamos solicitud");
		receptores.addReceptor(fichero, id);
		Mensaje m = new MensajeTexto(TMensaje.M_PEDIR_FICHERO, fichero);
		escritores.escribir(emisor, m);
	}
	
	private void enviarPreparadoSC(String nombreFicheroIPpuerto) throws InterruptedException {
		String[] split = nombreFicheroIPpuerto.split("-");
		ServerIO.log("El cliente " + id + " ya tiene preparado el archivo " + split[0]);
		
		String idReceptor = receptores.getReceptor(split[0]);
		ServerIO.log("Enviamos mensaje al receptor " + idReceptor + " indicando que ya tenemos el archivo " + split[0] + " preparado");
		Mensaje m = new MensajeTexto(TMensaje.M_PREPARADO_SC, nombreFicheroIPpuerto);
		escritores.escribir(idReceptor, m);
	}
}
