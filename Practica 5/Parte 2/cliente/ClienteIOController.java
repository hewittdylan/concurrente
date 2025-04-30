package cliente;

import concurrencia.Lock;
import concurrencia.LockTicket;

public class ClienteIOController {
	private static final Lock lock = new LockTicket();
	
	public static void print(String s) {
		lock.takeLock();
		System.out.println(s);
		lock.releaseLock();
	}
	
	public static void log(String s) {
		lock.takeLock();
		ClienteIO.log(s);
		lock.releaseLock();
	}
	
	public static void warning(String s) {
		lock.takeLock();
		ClienteIO.warning(s);
		lock.releaseLock();
	}
	
	public static void error(String s) {
		lock.takeLock();
		ClienteIO.error(s);
		lock.releaseLock();
	}
}
