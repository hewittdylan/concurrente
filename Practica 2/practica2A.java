interface  Lock {
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
}

class LockRompeEmpate implements  Lock {
    private volatile boolean[] flag = {false, false};
    private volatile int turno;

    @Override
    public void takeLock(int id) {  //0 Incrementar, 1 Decrementar
        int otro = 1 - id;
        flag[id] = true;  //Marco como usando
        turno = id;
        while (flag[otro] && turno == id) {
            //Espera activa
        }
    }

    @Override
    public void releaseLock(int id) {  //Marco que dejo de usarlo
        flag[id] = false;
    }
}

class Incrementador extends Thread {
    private final Entero entero;
    private final Lock lock;
    private final int id, n;

    public Incrementador(Entero entero, Lock lock, int id, int n) {
        this.entero = entero;
        this.lock = lock;
        this.id = id;
        this.n = n;
    }

    @Override
    public void run() {
        for (int i = 0; i < n; i++) {
            lock.takeLock(id);
            entero.incrementar();
            System.out.println("Incrementado: " + entero.getValor());
            lock.releaseLock(id);
        }
    }
}

class Decrementador extends Thread {
    private final Entero entero;
    private final Lock lock;
    private final int id, n;

    public Decrementador(Entero entero, Lock lock, int id, int n) {
        this.entero = entero;
        this.lock = lock;
        this.id = id;
        this.n = n;
    }

    @Override
    public void run() {
        for (int i = 0; i < n; i++) {
            lock.takeLock(id);
            entero.decrementar();
            System.out.println("Decrementado: " + entero.getValor());
            lock.releaseLock(id);
        }
    }
}

public class practica2A {
    public static void main(String[] args) {
        int N = 30;  //Número de incrementos/decrementos que realiza cada hilo
        
        Lock lock = new LockRompeEmpate();

        Entero entero = new Entero(0);

        Incrementador incrementador = new Incrementador(entero, lock, 0, N);
        Decrementador decrementador = new Decrementador(entero, lock, 1, N);

        incrementador.start();
        decrementador.start();

        try {
            incrementador.join();
            decrementador.join();
            System.out.println("Valor final del entero: " + entero.getValor());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
