

public class Monitor {
    private Entero n;

    public Monitor() {
        n = new Entero(0);
    }

    public synchronized void incrementar() {
        n.incrementar();
    }

    public synchronized void decrementar() {
        n.decrementar();
    }

    public synchronized Entero getEntero() {
        return n;
    }
}