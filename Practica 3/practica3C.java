import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Producto {
    private final int id;

    public Producto(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}

interface Almacen {
    void escribir(Producto producto, int pos, int idEscritor);
    Producto leer(int pos, int idLector);
}

class AlmacenLectoresEscritores implements Almacen {
    private final Producto[] buffer;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Semaphore testigo = new Semaphore(1);

    public AlmacenLectoresEscritores(int capacidad) {
        this.buffer = new Producto[capacidad];
    }

    @Override
    public void escribir(Producto producto, int pos, int idEscritor) {
        try {
            testigo.acquire();  //Exclusión mutua para escritores
            lock.writeLock().lock();
            buffer[pos] = producto;
            System.out.println("[ESCRITOR " + idEscritor + "] Escrito producto: " + producto.getId() + " en posición " + pos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
            testigo.release();
        }
    }

    @Override
    public Producto leer(int pos, int idLector) {
        lock.readLock().lock();
        Producto prod = buffer[pos];
        System.out.println("[LECTOR " + idLector + "] Leído producto: " + (prod != null ? prod.getId() : "null") + " de posición " + pos);
        lock.readLock().unlock();
        return prod;
    }
}

class Escritor extends  Thread {
    private final Almacen almacen;
    private final int P;
    private final int id;
    private final int capacidad;

    public Escritor(Almacen almacen, int P, int id, int capacidad) {
        this.almacen = almacen;
        this.P = P;
        this.id = id;
        this.capacidad = capacidad;
    }

    @Override
    public void run() {
        for (int i = 0; i < P; i++) {
            int pos = (i + id) % capacidad;
            Producto prod = new Producto(i);
            almacen.escribir(prod, pos, id);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Lector extends  Thread {
    private final Almacen almacen;
    private final int C;
    private final int id;
    private final int capacidad;

    public Lector(Almacen almacen, int C, int id, int capacidad) {
        this.almacen = almacen;
        this.C = C;
        this.id = id;
        this.capacidad = capacidad;
    }

    @Override
    public void run() {
        for (int i = 0; i < C; i++) {
            int pos = (i + id) % capacidad;
            almacen.leer(pos, id);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class practica3C {
    public static void main(String[] args) {
        int capacidad = 5;
        Almacen almacen = new AlmacenLectoresEscritores(capacidad);
        int numEscritores = 2;
        int numLectores = 2;
        int P = 3;  //Num productos por productor
        int C = 3;  //Num productos por consumidor

        Thread[] escritores = new Thread[numEscritores];
        Thread[] lectores = new Thread[numLectores];

        for (int i = 0; i < numEscritores; i++) {
            escritores[i] = new Escritor(almacen, P, i, capacidad);
            escritores[i].start();
        }

        for (int i = 0; i < numLectores; i++) {
            lectores[i] = new Lector(almacen, C, i, capacidad);
            lectores[i].start();
        }

        for (Thread t : escritores) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Thread t : lectores) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
