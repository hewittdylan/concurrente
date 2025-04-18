import java.util.Random;

public class Escritor extends Thread {
    private int P;
    private Producto[] productos;
    private String id;
    private int n;  //Número de productos escritos
    private MonitorCL m;

    public Escritor(MonitorCL m, int P, String id, Producto[] productos) {
        this.m = m;
        this.P = P;
        this.id = id;
        this.productos = productos;
        n = 0;
    }

    public void run() {
        Random rand = new Random();
        for (int i = 0; i < P; i++) {
            String id = this.id + "-" + Integer.toString(n++);
            Producto p = new Producto(id);
            m.requestWrite();
            escribir(p, rand.nextInt(productos.length));
            m.releaseWrite();
        }
    }

    private void escribir(Producto p, int pos) {
        productos[pos] = p;
        System.out.println("Almacen [" + pos + "] <- " + p.getId());
    }
}