package servidor;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerIO {
	private static final String RED = "\u001B[31m";
	private static final String YELLOW = "\u001B[33m";
	private static final String RESET = "\u001B[0m";

	private static final Logger logger = Logger.getLogger(ServerIO.class.getName());
	private static FileHandler gestorArchivo;
	
	static {
		try {
			gestorArchivo = new FileHandler("serverLog.log");
			logger.addHandler(gestorArchivo);
			SimpleFormatter formatter = new SimpleFormatter();
			gestorArchivo.setFormatter(formatter);
		} catch (IOException e) {
			System.err.println("Error iniciando el logger del servidor");
			e.printStackTrace();
		}
	}
	
	public static void log(String s) {
		synchronized (logger) {
			logger.info(s);
		}
	}
	
	public static void warning(String s) {
		synchronized (logger) {
			logger.log(Level.WARNING, YELLOW + s + RESET);
		}
	}
	
	public static void error(String s) {
		synchronized (logger) {
			logger.log(Level.SEVERE, RED + s + RESET);
		}
	}
}
