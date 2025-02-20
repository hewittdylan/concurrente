class LockRompeEmpate {
    private volatile boolean[] flag = {false, false};
    private volatile int turno;

    public void takeLock(int id) {
        int otro = 1 - id;
        flag[id] = true;  //Marco como usando
        turno = id;
        while (flag[otro] && turno == id) {
            //Espera activa
        }
    }

    public void releaseLock(int id) {  //Marco que dejo de usarlo
        flag[id] = false;
    }
}

class Contador {
    public int valor = 0;
}

class Incrementador extends Thread {
    private Contador contador;
    private LockRompeEmpate lock;
    private int id, n;

    public Incrementador(Contador contador, LockRompeEmpate lock, int id, int n) {
        this.contador = contador;
        this.lock = lock;
        this.id = id;
        this.n = n;
    }

    @Override
    public void run() {
        for (int i = 0; i < n; i++) {
            lock.takeLock(id);
            contador.valor++;
            lock.releaseLock(id);
        }
    }
}

class Decrementador extends Thread {
    private Contador contador;
    private LockRompeEmpate lock;
    private int id, n;

    public Decrementador(Contador contador, LockRompeEmpate lock, int id, int n) {
        this.contador = contador;
        this.lock = lock;
        this.id = id;
        this.n = n;
    }

    @Override
    public void run() {
        for (int i = 0; i < n; i++) {
            lock.takeLock(id);
            contador.valor--;
            lock.releaseLock(id);
        }
    }
}

public class practica2A {
    public static void main(String[] args) {
        Contador contador = new Contador();
        LockRompeEmpate lock = new LockRompeEmpate();
        int n = 100000;

        Thread inc = new Incrementador(contador, lock, 0, n);
        Thread decr = new Decrementador(contador, lock, 1, n);

        inc.start();
        decr.start();

        try {
            inc.join();
            decr.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.print("Valor final del contador: " + contador.valor);
    }
}
