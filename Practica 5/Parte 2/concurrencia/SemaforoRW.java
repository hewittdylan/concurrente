package concurrencia;

import java.util.concurrent.Semaphore;

public class SemaforoRW implements ReadWriteController {
    private Semaphore e, r, w;
    private Entero nr, nw, dr, dw;

    public SemaforoRW() {
        e = new Semaphore(1);
        r = new Semaphore(0);
        w = new Semaphore(0);
        nr = new Entero(0);
        nw = new Entero(0);
        dr = new Entero(0);
        dw = new Entero(0);
    }

    @Override
    public void request_read() throws InterruptedException {
        e.acquire();
        if (0 < nw.getValor() || 0 < dw.getValor()) {
            dr.incrementar();
            e.release();
            r.acquire();
        }
        nr.incrementar();
        if (0 < dr.getValor()) {
            dr.decrementar();
            r.release();
        } else {
            e.release();
        }
    }

    @Override
    public void release_read() throws InterruptedException {
        e.acquire();
        nr.decrementar();
        if (nr.getValor() == 0 && 0 < dw.getValor()) {
            dw.decrementar();
            w.release();
        } else {
            e.release();
        }
    }

    @Override
    public void request_write() throws InterruptedException {
        e.acquire();
        if (0 < nr.getValor() || 0 < nw.getValor()) {
            dw.incrementar();
            e.release();
            w.acquire();
        }
        nw.incrementar();
        e.release();
    }

    @Override
    public void release_write() throws InterruptedException {
        e.acquire();
        nw.decrementar();
        if (0 < dr.getValor()) {
            dr.decrementar();
            r.release();
        }
        else if (0 < dw.getValor()) {
            dw.decrementar();
            w.release();
        }
        else {
            e.release();
        }
    }

}