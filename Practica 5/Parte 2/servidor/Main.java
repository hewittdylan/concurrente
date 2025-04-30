package servidor;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		ServerIO.log("Iniciando servidor");
		Servidor s = null;
		try {
			s = new Servidor();
		} catch (IOException e) {
			ServerIO.error("Error iniciando servidor");
			System.exit(1);
		}
		s.start();
	}
}
