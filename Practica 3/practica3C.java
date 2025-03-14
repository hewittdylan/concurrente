import java.util.Random;
import java.util.concurrent.Semaphore;

class Contador {
    private volatile int valor;

    public Contador() {
        this.valor = 0;
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

class Producto {
    private final String id;

    public Producto(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}

interface Almacen {
    void escribir(Producto producto, int pos);
    void leer(int pos);
    int size();
}

class AlmacenEL implements Almacen {
    private final Producto[] productos;
    private final Semaphore writers;
    private final Semaphore readers;
    private final Semaphore mutex;  //Para exlusión mutua
    private final Contador activeWriters;
    private final Contador activeReaders;
    private final Contador waitingWriters;
    private final Contador waitingReaders;
    private final int capacidad;

    public AlmacenEL(Semaphore writers, Semaphore readers, Semaphore mutex, int capacidad) {
        this.writers = writers;
        this.readers = readers;
        this.mutex = mutex;
        this.productos = new Producto[capacidad];
        this.capacidad = capacidad;
        this.activeWriters = new Contador();
        this.activeReaders = new Contador();
        this.waitingWriters = new Contador();
        this.waitingReaders = new Contador();
    }

    @Override
    public int size() {
        return this.capacidad;
    }

    @Override
    public void escribir(Producto producto, int pos) {
        try {
            mutex.acquire();  //Exclusión mutua

            if (0 < activeWriters.getValor() || 0 < activeReaders.getValor()) {  //Si hay escritores o lectores activos
                waitingWriters.incrementar();  //Escritor en espera
                mutex.release();
                writers.acquire();  //Espera su turno
            } 

            activeWriters.incrementar();  //Escritor activo
            mutex.release();

            //Escritura del producto
            productos[pos] = producto;
            System.out.println("[ESCRITOR] Posición: " + pos + ", contenido: " + producto.getId());

            mutex.acquire();
            activeWriters.decrementar();

            //Prioridad a lectores en espera
            if (0 < waitingReaders.getValor()) {
                waitingReaders.decrementar();
                readers.release();
            }

            //Si hay escritores en espera, los desbloquea
            if (0 < waitingWriters.getValor()) {
                waitingWriters.decrementar();
                writers.release();
            } else {
                mutex.release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void leer(int pos) {
        try {
            mutex.acquire();  //Exclusión mutua

            if (0 < activeReaders.getValor()) {  //Si hay escritores activos
                waitingReaders.incrementar();  //Aumenta el número de lectores en esperaa
                mutex.release();  //Libera exclusión mutua
                readers.acquire();  //Espera hasta que no haya escritores
            }

            activeReaders.incrementar();  //Aumenta el número de lectores activos

            if (0 < waitingReaders.getValor()) {  //Si hay lectores en espera
                waitingReaders.decrementar();
                readers.release();  //Libera otro lector
            } else {
                mutex.release();
            }

            System.out.println("[LECTOR]   Posición: " + pos + ", contenido: " + (productos[pos] == null ? "null" : productos[pos].getId()));

            mutex.acquire();
            activeReaders.decrementar();

            if (activeReaders.getValor() == 0 && 0 < waitingReaders.getValor()) {  //Si es el último lector y hay escritores en espera
                waitingReaders.decrementar();
                writers.release();  //Libera un escritor
            } else {
                mutex.release();
            }

        } catch (Exception e) {

        }
    }
}

class Escritor extends  Thread {
    private final Almacen almacen;
    private final int P;
    private final int id;

    public Escritor(Almacen almacen, int P, int id) {
        this.almacen = almacen;
        this.P = P;
        this.id = id;
    }

    @Override
    public void run() {
        Random rand = new Random();
        for (int i = 0; i < P; i++) {
            String idProducto = Integer.toString(id) + " - " + Integer.toString(i);
            Producto prod = new Producto(idProducto);
            almacen.escribir(prod, rand.nextInt(almacen.size()));
        }
    }
}

class Lector extends  Thread {
    private final Almacen almacen;
    private final int C;
    private final int id;

    public Lector(Almacen almacen, int C, int id) {
        this.almacen = almacen;
        this.C = C;
        this.id = id;
    }

    @Override
    public void run() {
        Random rand = new Random();
        for (int i = 0; i < C; i++) {
            almacen.leer(rand.nextInt(almacen.size()));
        }
    }
}

public class practica3C {
    public static void main(String[] args) {
        int numEscritores = 5;
        int numLectores = 5;
        int P = 2;  //Num productos por escritor
        int C = 5;  //Num productos por lector
        int capacidadAlmacen = 3;

        Semaphore writers = new Semaphore(0);
        Semaphore readers = new Semaphore(0);
        Semaphore mutex = new Semaphore(1);

        Almacen almacen = new AlmacenEL(writers, readers, mutex, capacidadAlmacen);

        Escritor[] escritores = new Escritor[numEscritores];
        Lector[] lectores = new Lector[numLectores];

        for (int i = 0; i < numEscritores; i++) {
            escritores[i] = new Escritor(almacen, P, i);
            escritores[i].start();
        }

        for (int i = 0; i < numLectores; i++) {
            lectores[i] = new Lector(almacen, C, i);
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
