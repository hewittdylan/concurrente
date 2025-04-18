public class Monitor {
    private Entero nr, nw;

    public Monitor(int numProductos) {
        nr = new Entero(0);
        nw = new Entero(0);
    }

    public synchronized void requestRead() {
        while (0 < nw.getValor()) {
            try {
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        nr.incrementar();
    }

    public synchronized void requestWrite() {
        while (0 < nw.getValor() || 0 < nr.getValor()) {
            try {
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        nw.incrementar();
    }

    public synchronized void releaseRead() {
        nr.decrementar();
        if (nr.getValor() == 0) notifyAll();
    }

    public synchronized void releaseWrite() {
        nw.decrementar();
        notifyAll();
    }
}