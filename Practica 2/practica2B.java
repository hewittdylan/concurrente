import java.util.concurrent.atomic.AtomicInteger;

interface Lock {
    void takeLock(int id);
    void releaseLock(int id);
}

class Entero {
    private volatile int valor = 0;

    public Entero(int valor) {
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
    protected  final Entero entero;
    private final Lock lock;
    private final int id;
    private final int N;

    public Modificacion(Entero entero, Lock lock, int id, int N) {
        this.entero = entero;
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

    public Incrementador(int N, Entero entero, Lock lock, int id) {
        super(entero, lock, id, N);
    }

    @Override
    public void critical() {
        super.entero.incrementar();
    }
}

class Decrementador extends Modificacion {

    public Decrementador(int N, Entero entero, Lock lock, int id) {
        super(entero, lock, id, N);
    }

    @Override
    public void critical() {
        super.entero.decrementar();
    }
}

class LockRompeEmpate implements  Lock {
    private Entero[] in, last;
    private final int N;

    public LockRompeEmpate(int N) {
        this.N = N;
        in = new Entero[N];
        last = new Entero[N];
        inicializar(in);
        inicializar(last);
    }

    private void inicializar(Entero[] array) {
        for (int i = 0; i < N; i++) {
            array[i] = new Entero(0);
        }
    }

    @Override
    public void takeLock(int i) {
        for (int j = 1; j <= N; j++) {
            in[i - 1].setValor(j);
            last[j - 1].setValor(i);

            for (int k = 1; k <= N; k++) {
                if (k == i) continue;
                while (in[i - 1].getValor() <= in[k - 1].getValor() && last[j - 1].getValor() == i) {
                    //Mientras la etapa del proceso i sea menor que la de k
                    //Y el id del último proceso que llegó a la etapa j sea i
                }
            }
        }
    }

    @Override
    public void releaseLock(int i) {
        in[i - 1].setValor(0);
    }
}

class LockTicket implements Lock {
    private Entero siguiente;
    private AtomicInteger numero;
    private Entero[] turno;
    private final int N;

    public LockTicket(int N) {
        this.N = N;
        siguiente = new Entero(1);
        numero = new AtomicInteger(1);
        turno = new Entero[N];
        inicializar(turno);
    }

    private void inicializar(Entero[] array) {
        for (int i = 0; i < N; i++) {
            array[i] = new Entero(0);
        }
    }

    @Override
    public void takeLock(int i) {
        turno[i].setValor(numero.getAndAdd(1));
        while(turno[i].getValor() != siguiente.getValor()) {}
    }

    @Override
    public void releaseLock(int i) {
        siguiente.incrementar();
    }
}

class LockBakery implements Lock {
    private Entero[] turno;
    private final int N;

    public LockBakery(int N) {
        this.N = N;
        turno = new Entero[N];
        inicializar(turno);
    }

    private void inicializar(Entero[] array) {
        for (int i = 0; i < N; i++) {
            array[i] = new Entero(0);
        }
    }

    private int maxs(Entero[] array) {
        int max = -1;
        for (int i = 0; i < N; i++) {
            if (max < array[i].getValor()) {
                max = array[i].getValor();
            }
        }
        return max;
    }

    private boolean operator(Entero turno1, int i, Entero turno2, int j) {
        if (turno2.getValor() < turno1.getValor()) return true;
        return turno1.getValor() == turno2.getValor() && j < i;
    }

    @Override
    public void takeLock(int i) {
        turno[i].setValor(1);
        turno[i].setValor(maxs(turno) + 1);

        for (int j = 0; j < N; j++) {
            if (j == i) continue;
            while (turno[j].getValor() != 0 && operator(turno[i], i, turno[j], j)) {}
        }
    }

    @Override
    public void releaseLock(int i) {
        turno[i].setValor(0);
    }
}

public class practica2B {
    public static void main(String[] args) {
        int N = 1000;  //Número de incrementos/decrementos que realiza cada hilo
        int M = 3;  //Número de hilos de cada tipo

        Modificacion[] modificaciones = new Modificacion[M];
        Lock lock = new LockBakery(N);

        Entero entero = new Entero(0);

        for (int i = 0; i < M / 2; i++) {
            //Rompe empate
            //modificaciones[i] = new Incrementador(N, entero, lock, i + 1);
            //Bakery
            modificaciones[i] = new Incrementador(N, entero, lock, i);
        }

        for (int i = M / 2; i < M; i++) {
            //Rompe empate
            //modificaciones[i] = new Decrementador(N, entero, lock, i + 1);
            //Bakery
            modificaciones[i] = new Decrementador(N, entero, lock, i);
        }

        for (int i = 0; i < M; i++) modificaciones[i].start();

        try {
            for (int i = 0; i < M; i++) modificaciones[i].join();
            System.out.print("Valor final del entero: " + entero.getValor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}