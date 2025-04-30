package concurrencia;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorRW implements ReadWriteController {
    private final Lock lock;
    private final Condition read, write;
    private Entero nr, nw;

    public MonitorRW() {
        lock = new ReentrantLock();
        read = lock.newCondition();
        write = lock.newCondition();
        nr = new Entero(0);
        nw = new Entero(0);
    }

    @Override
    public void request_read() throws InterruptedException {
        lock.lock();
        while (0 < nw.getValor()) {
            read.await();
        }
        nr.incrementar();
        lock.unlock();
    }

    @Override
    public void release_read() throws InterruptedException {
        lock.lock();
        nr.decrementar();
        if (nr.getValor() == 0) write.signal();
        lock.unlock();
    }

    @Override
    public void request_write() throws InterruptedException {
        lock.lock();
        while (0 < nw.getValor() || 0 < nw.getValor()) {
            write.await();
        }
        nw.incrementar();
        lock.unlock();
    }

    @Override
    public void release_write() throws InterruptedException {
        lock.lock();
        nw.decrementar();
        write.signal();
        read.signal();
        lock.unlock();
    }
} 
