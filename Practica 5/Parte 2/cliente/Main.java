package cliente;

import java.io.IOException;
import java.util.Scanner;

public class Main {
	private static Scanner scanner;
	private static Cliente cliente;
	private static boolean conectado = true;
	
	public static void main(String[] args) {
		scanner = new Scanner(System.in);
		ClienteIOController.print("Introduzca nombre de usuario: ");
		String nombre = scanner.next();
		scanner.nextLine();
		try {
			cliente = new Cliente(nombre);
		} catch (ClassNotFoundException | IOException e) {
			ClienteIOController.error("Error creando cliente. Cerrando aplicación");
			scanner.close();
			return;
		}
		
		while(conectado) {
			int acc = menu();
			accion(acc);
		}
		scanner.close();
	}
	
	private static int menu() {
		int a;
		ClienteIOController.print(" - Menú Principal - ");
		ClienteIOController.print("1 - Consultar la información del sistema");
		ClienteIOController.print("2 - Descargar información");
		ClienteIOController.print("3 - Desconectar");
		ClienteIOController.print("Acción: ");
		try {
			a = scanner.nextInt();
		} catch (Exception e) {
			scanner.nextLine();
			return -1;
		}
		scanner.nextLine();
		return a;
	}
	
	private static void accion(int a) {
		switch (a) {
		case 1:
			try {
				cliente.consultaInfo();
			} catch (IOException e) {
				ClienteIOController.error("Error consultado la lista de usuarios");
			}
			break;
		case 2:
			ClienteIOController.print("Introduce el nombre del fichero que descargar: ");
			String fichero = scanner.nextLine();
			try {
				cliente.descargarInfo(fichero);
			} catch (IOException e) {
				ClienteIOController.error("Error descargando el fichero");
			}
			break;
		case 3:
			try {
				cliente.desconectar();
			} catch (InterruptedException | IOException e) {
				ClienteIOController.error("Error desconectándose del servidor");
			}
			conectado = false;
			break;
		default:
			ClienteIOController.error("Acción desconocida");
		}
	}
}
