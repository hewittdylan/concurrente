import java.util.Random;

public class Lector extends Thread {
    private Random rand;
    private int C;
    private Producto[] productos;
    private Monitor m;

    public Lector(Monitor m, Producto[] productos, int C) {
        this.C = C;
        this.productos = productos;
        this.m = m;
    }

    public void run() {
        rand = new Random();
        for (int i = 0; i < C; i++) {
            m.requestRead();
            leer(rand.nextInt(productos.length));
            m.releaseRead();
        }
    }

    private void leer(int pos) {
        System.out.println("Almacen [" + pos + "] -> " + productos[pos].getId());
    }
}