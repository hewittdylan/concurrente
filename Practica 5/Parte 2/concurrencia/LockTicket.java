package concurrencia;

import java.util.concurrent.atomic.AtomicInteger;

public class LockTicket extends Lock {
    private Entero siguiente;
    private AtomicInteger num;
    
    public LockTicket() {
    	super();
    	num = new AtomicInteger(1);
        siguiente = new Entero(1);
    }

    @Override
    public void takeLock() {
        Integer turno = num.getAndAdd(1);
        if (turno == N) num.addAndGet(-N);
        else if (N < turno) turno -= N;
        while(turno != siguiente.getValor()) {}
    }

    @Override
    public void releaseLock() {
        siguiente.incrementar();
        siguiente.setValor(siguiente.getValor() % N);
    }
}
