package cliente;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteIO {
	private static final String RED = "\u001B[31m";
	private static final String YELLOW = "\u001B[33m";
	private static final String RESET = "\u001B[0m";
	
	private static final Logger logger = Logger.getLogger(ClienteIO.class.getName());
	
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
