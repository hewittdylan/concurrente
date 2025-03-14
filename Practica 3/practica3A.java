import java.util.concurrent.Semaphore;

class Entero {
    private volatile int valor;

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
    private final Semaphore semaphore;
    private final int id;
    private final int N;

    public Modificacion(Entero entero, Semaphore semaphore, int id, int N) {
        this.entero = entero;
        this.semaphore = semaphore;
        this.id = id;
        this.N = N;
    }

    protected  abstract void critical();

    @Override
    public void run() {
        for (int i = 0; i < N; i++) {
            try {
                semaphore.acquire();
                critical();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }
    } 
}

class Incrementador extends Modificacion {

    public Incrementador(Entero entero, Semaphore semaphore, int id, int N) {
        super(entero, semaphore, id, N);
    }

    @Override
    protected void critical() {
        super.entero.incrementar();
    }
}

class Decrementador extends Modificacion {

    public Decrementador(Entero entero, Semaphore semaphore, int id, int N) {
        super(entero, semaphore, id, N);
    }

    @Override
    protected void critical() {
        super.entero.decrementar();
    }
}

public class practica3A {
    public static void main(String[] args) {
        
        int N = 1000;  //Número de incrementos
        int M = 5;  //Número de hilos de cada tipo

        Modificacion[] modificaciones = new Modificacion[2 * M];
        Semaphore semaphore = new Semaphore(1);
        Entero entero = new Entero(0);

        for (int i = 0; i < M; i++) {
            modificaciones[i] = new Incrementador(entero, semaphore, i, N);
            modificaciones[M + i] = new Decrementador(entero, semaphore, M + i, N);
        }

        for (Modificacion m : modificaciones) m.start();
        for (Modificacion m : modificaciones) {
            try {
                m.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.print("Valor final del contador: " + entero.getValor());
    }
}