import java.util.concurrent.Semaphore;

class Contador {
    private int valor = 0;
    private final Semaphore semaforo;

    public Contador() {
        this.semaforo = new Semaphore(1);
    }

    public void incrementar() {
        try {
            semaforo.acquire();  //Bloqueamos el acceso
            valor++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforo.release();  //Liberamos el acceso
        }
    }

    public void decrementar() {
        try {
            semaforo.acquire();  //Bloqueamos el acceso
            valor--;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforo.release();  //Liberamos el acceso
        }
    }

    public int getValor() {
        return valor;
    }
}

class Incrementador extends Thread {
    private final Contador contador;
    private final int N;

    public Incrementador(Contador contador, int N) {
        this.contador = contador;
        this.N = N;
    }

    @Override
    public void run() {
        for (int i = 0; i < N; i++) contador.incrementar();
    }
}

class Decrementador extends Thread {
    private final Contador contador;
    private final int N;

    public Decrementador(Contador contador, int N) {
        this.contador = contador;
        this.N = N;
    }

    @Override
    public void run() {
        for (int i = 0; i < N; i++) contador.decrementar();
    }
}

public class practica3A {
    public static void main(String[] args) {
        Contador contador = new Contador();
        int N = 1000;
        int M = 5;  //Número de hilos

        Thread[] threads = new Thread[2 * M];

        for (int i = 0; i < M; i++) {
            threads[i] = new Incrementador(contador, N);
            threads[M + i] = new Decrementador(contador, N);
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.print("Valor final del contador: " + contador.getValor());
    }
}