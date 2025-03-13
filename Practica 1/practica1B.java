class Entero {
    public int n = 0;
};

class Incrementador extends Thread {
    private Entero ent;
    private int n;

    public Incrementador(Entero ent, int n) {
        this.ent = ent;
        this.n = n;
    }

    @Override
    public void run() {
        for (int i = 0; i < n; i++) {
            ent.n++;
        }
    }
}

class Decrementador extends Thread {
    private Entero ent;
    private int n;

    public Decrementador(Entero ent, int n) {
        this.ent = ent;
        this.n = n;
    }

    @Override
    public void run() {
        for (int i = 0; i < n; i++) {
            ent.n--;
        }
    }
}

public class practica1B {
    public static void main(String[] args) {
        int M = 50, N = 10000; //M threads de cada tipo, cada uno con N operaciones
        Entero entero = new Entero();

        Thread[] threads = new Thread[2 * M];

        for (int i = 0; i < M; i++) {
            threads[i] = new Incrementador(entero, N);
            threads[M + i] = new Decrementador(entero, N);
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Valor final de n: " + entero.n);
    }
}
