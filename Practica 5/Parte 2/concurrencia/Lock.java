package concurrencia;

public abstract class Lock {
	protected int N;  //Max threads
	
	public Lock() {
		this.N = 10000;
	}

    public abstract void takeLock();
    public abstract void releaseLock();
}
