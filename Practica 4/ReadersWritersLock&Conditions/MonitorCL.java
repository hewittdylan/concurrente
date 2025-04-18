
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorCL {
    private Entero nr, nw;
    private final Lock lock;
    private final Condition okRead;
    private final Condition okWrite;

    public MonitorCL(int numProductos) {
        nr = new Entero(0);
        nw = new Entero(0);
        lock = new ReentrantLock();
        okRead = lock.newCondition();
        okWrite = lock.newCondition();
    }

    public void requestRead() {
        lock.lock();
        while (0 < nw.getValor()) {
            try {
                okRead.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        nr.incrementar();
        lock.unlock();
    }

    public void requestWrite() {
        lock.lock();
        while (0 < nw.getValor() || 0 < nr.getValor()) {
            try {
                okWrite.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        nw.incrementar();
        lock.unlock();
    }

    public void releaseRead() {
        lock.lock();
        nr.decrementar();
        if (nr.getValor() == 0) okWrite.signal();
        lock.unlock();
    }

    public void releaseWrite() {
        lock.lock();
        nw.decrementar();
        okWrite.signal();
        okRead.signalAll();
        lock.unlock();
    }
}