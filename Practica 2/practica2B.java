interface Lock {
    void takeLock(int id);
    void releaseLock(int id);
}

class Contador {
    private volatile int valor = 0;

    public Contador(int valor) {
        this.valor = valor;
    }

    public void incrementar() {
        this.valor++;
    }

    public void decrementar() {
        this.valor--;
    }

    public int getValor() {
        return this.valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
}

abstract class Modificacion extends Thread {
    protected  final Contador contador;
    private final Lock lock;
    private final int id;
    private final int N;

    public Modificacion(Contador contador, Lock lock, int id, int N) {
        this.contador = contador;
        this.lock = lock;
        this.id = id;
        this.N = N;
    }

    public abstract void critical();

    @Override
    public void run() {
        for(int i = 0; i < N; i++) {
            lock.takeLock(id);
            critical();
            lock.releaseLock(id);
        }
    } 
}

class Incrementador extends Modificacion {

    public Incrementador(int N, Contador contador, Lock lock, int id) {
        super(contador, lock, id, N);
    }

    @Override
    public void critical() {
        super.contador.incrementar();
    }
}

class Decrementador extends Modificacion {

    public Decrementador(int N, Contador contador, Lock lock, int id) {
        super(contador, lock, id, N);
    }

    @Override
    public void critical() {
        super.contador.decrementar();
    }
}