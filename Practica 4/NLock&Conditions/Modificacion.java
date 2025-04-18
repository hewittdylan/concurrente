public abstract class Modificacion extends Thread {
    protected MonitorLC m;
    protected int id;
    protected int N;

    public Modificacion(MonitorLC m, int id, int n) {
        super();
        this.m = m;
        this.id = id;
        this.N = n;
    }

    protected abstract void critical();

    public void run() {
        for (int i = 0; i < N; i++) critical();
    }
}