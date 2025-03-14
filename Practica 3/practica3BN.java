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
    void almacenar(Producto producto);
    Producto extraer();
}

class AlmacenProductos implements Almacen {
    private Producto[] productos;
    private final Semaphore lleno;
    private final Semaphore vacio;
    private final Semaphore mutexP;
    private final Semaphore mutexC;
    private final int capacidad;
    private final Contador ini;
    private final Contador fin;

    public AlmacenProductos(Semaphore lleno, Semaphore vacio, Semaphore mutexP, Semaphore mutexC, int capacidad) {
        this.lleno = lleno;
        this.vacio = vacio;
        this.mutexP = mutexP;
        this.mutexC = mutexC;
        this.capacidad = capacidad;
        this.productos = new Producto[capacidad];
        this.ini = new Contador(capacidad);
        this.fin = new Contador(capacidad);
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

public class practica3BN {
    public static void main(String[] args) {
        int numProductores = 3;
        int numConsumidores = 3;
        int P = 10;  //Num productos por productor
        int C = 10;  //Num productos por consumidor
        int capacidadAlmacen = 2;

        Semaphore lleno = new Semaphore(0);
        Semaphore vacio = new Semaphore(capacidadAlmacen);
        Semaphore mutexP = new Semaphore(1);
        Semaphore mutexC = new Semaphore(1);
        Almacen almacen = new AlmacenProductos(lleno, vacio, mutexP, mutexC, capacidadAlmacen);

        Productor[] productores = new Productor[numProductores];
        Consumidor[] consumidores = new Consumidor[numConsumidores];

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
