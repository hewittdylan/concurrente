import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

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
    void almacenar(Producto producto, int idProductor);
    Producto extraer(int idConsumidor);
}

class AlmacenBufferN implements Almacen {
    private final Queue<Producto> buffer;
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore lleno;
    private final Semaphore vacio;

    public AlmacenBufferN(int capacidad) {
        this.buffer = new LinkedList<>();
        this.lleno = new Semaphore(0);
        this.vacio = new Semaphore(capacidad);
    }

    @Override
    public void almacenar(Producto producto, int idProductor) {
        try {
            vacio.acquire();  //Si el buffer está lleno espera
            mutex.acquire();
            buffer.add(producto);
            System.out.println("[PRODUCTOR " + idProductor + "] Almacenado producto: " + producto.getId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            lleno.release();  //Notifica que hay un producto disponible
        }
    }

    @Override
    public Producto extraer(int idConsumidor) {
        Producto prod = null;
        try {
            lleno.acquire();  //Espera si el buffer está vacío
            mutex.acquire();
            prod = buffer.poll();
            System.out.println("[CONSUMIDOR " + idConsumidor + "] Consumido producto: " + prod.getId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            vacio.release();  //Notifica que el buffer está vacío
        }
        return prod;
    }
}

class Productor extends  Thread {
    private final Almacen almacen;
    private final int P;
    private final int id;

    public Productor(Almacen almacen, int P, int id) {
        this.almacen = almacen;
        this.P = P;
        this.id = id;
    }

    @Override
    public void run() {
        for (int i = 0; i < P; i++) {
            Producto prod = new Producto(i);
            almacen.almacenar(prod, id);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Consumidor extends  Thread {
    private final Almacen almacen;
    private final int C;
    private final int id;

    public Consumidor(Almacen almacen, int C, int id) {
        this.almacen = almacen;
        this.C = C;
        this.id = id;
    }

    @Override
    public void run() {
        for (int i = 0; i < C; i++) {
            almacen.extraer(id);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class practica3B {
    public static void main(String[] args) {
        int capacidad = 5;
        Almacen almacen = new AlmacenBufferN(capacidad);
        int numProductores = 3;
        int numConsumidores = 3;
        int P = 11;  //Num productos por productor
        int C = 11;  //Num productos por consumidor

        Thread[] productores = new Thread[numProductores];
        Thread[] consumidores = new Thread[numConsumidores];

        for (int i = 0; i < numProductores; i++) {
            productores[i] = new Productor(almacen, P, i);
            productores[i].start();
        }

        for (int i = 0; i < numConsumidores; i++) {
            consumidores[i] = new Consumidor(almacen, C, i);
            consumidores[i].start();
        }

        for (Thread t : productores) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Thread t : consumidores) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
