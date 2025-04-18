import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorLC {
    private Entero n;
    private final Lock lock = new ReentrantLock();

    public MonitorLC() {
        n = new Entero(0);
    }

    public void incrementar() {
        lock.lock();
        n.incrementar();
        lock.unlock();
    }

    public void decrementar() {
        lock.lock();
        n.decrementar();
        lock.unlock();
    }

    public Entero getEntero() {
        return n;
    }
}