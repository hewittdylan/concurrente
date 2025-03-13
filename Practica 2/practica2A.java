interface  Lock {
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
    private final Contador contador;
    private final Lock lock;
    private final int id, n;

    public Incrementador(Contador contador, Lock lock, int id, int n) {
        this.contador = contador;
        this.lock = lock;
        this.id = id;
        this.n = n;
    }

    @Override
    public void run() {
        for (int i = 0; i < n; i++) {
            lock.takeLock(id);
            contador.incrementar();
            System.out.print("Incrementado: " + contador.getValor());
            lock.releaseLock(id);
        }
    }
}

class Decrementador extends Thread {
    private final Contador contador;
    private final Lock lock;
    private final int id, n;

    public Decrementador(Contador contador, Lock lock, int id, int n) {
        this.contador = contador;
        this.lock = lock;
        this.id = id;
        this.n = n;
    }

    @Override
    public void run() {
        for (int i = 0; i < n; i++) {
            lock.takeLock(id);
            contador.decrementar();
            System.out.print("Decrementado: " + contador.getValor());
            lock.releaseLock(id);
        }
    }
}

public class practica2A {
    public static void main(String[] args) {
        int N = 1000;  //Número de incrementos/decrementos que realiza cada hilo
        int M = 3;  //Número de hilos de cada tipo

        Incrementador[] incrementadores = new Incrementador[M];
        Decrementador[] decrementadores = new Decrementador[M];
        
        Lock lock = new LockRompeEmpate();

        Contador contador = new Contador(0);

        for (int i = 0; i < M; i++) {  //Creamos threads
            incrementadores[i] = new Incrementador(contador, lock, 0, N);
            decrementadores[i] = new Decrementador(contador, lock, 1, N);
        }

        for (int i = 0; i < M; i++) {  //Iniciamos threads
            incrementadores[i].start();
            decrementadores[i].start();
        }

        try {
            for (int i = 0; i < M; i++) {  //Esperamos a que acaben los threads
                incrementadores[i].join();
                decrementadores[i].join();
            }
            System.out.print("Valor final del contador: " + contador.getValor());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
