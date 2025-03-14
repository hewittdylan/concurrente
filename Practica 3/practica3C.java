import java.util.concurrent.Semaphore;

class Contador {
    private final int maximo;
    private volatile int valor;

    public Contador(int maximo) {
        this.maximo = maximo;
        this.valor = 0;
    }

    public void incrementar() {
        this.valor = (this.valor + 1) % this.maximo;
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
    Producto leer(int pos);
}

class AlmacenLibros implements Almacen {
    private Producto[] productos;
    private final Semaphore writers;
    private final Semaphore readers;
    private final Semaphore mutex;
    private final int capacidad;

    public AlmacenLibros(Semaphore writers, Semaphore readers, Semaphore mutex, int capacidad) {
        this.writers = writers;
        this.readers = readers;
        this.mutex = mutex;
        this.capacidad = capacidad;
        this.productos = new Producto[capacidad];
    }

    @Override
    public void almacenar(Producto producto) {
        try {
            vacio.acquire();  //Si el buffer está lleno espera
            mutexP.acquire();
            productos[fin.getValor()] = producto;  //Guardamos los elementos por el final
            fin.incrementar();
            System.out.println("[PRODUCTOR] Almacenado producto: " + producto.getId());
            mutexP.release();
            lleno.release();  //Notifica que hay un producto disponible
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Producto extraer() {
        Producto prod = null;
        try {
            lleno.acquire();  //Espera si el buffer está vacío
            mutexC.acquire();
            prod = productos[ini.getValor()];
            ini.incrementar();
            System.out.println("[CONSUMIDOR] Consumido producto: " + (prod == null ? "null" : prod.getId()));
            mutexC.release();
            vacio.release();  //Notifica que el buffer está vacío
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            String idProducto = Integer.toString(id) + " - " + Integer.toString(i);
            Producto prod = new Producto(idProducto);
            almacen.almacenar(prod);
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
            almacen.extraer();
        }
    }
}

public class practica3C {
    public static void main(String[] args) {
        int numEscritores = 100;
        int numLectores = 50;
        int P = 1;  //Num productos por escritor
        int C = 10;  //Num productos por lector
        int capacidadAlmacen = 10;

        Semaphore writers = new Semaphore(0);
        Semaphore readers = new Semaphore(0);
        Semaphore mutex = new Semaphore(1);

        Almacen almacen = new AlmacenProductos(lleno, vacio, mutexP, mutexC, capacidadAlmacen);

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
