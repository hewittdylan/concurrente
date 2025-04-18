public class Productor extends Thread {
    private Monitor m;
    private int n;  //Número de productos creados (id)
    private int P;  //Número de productos que produce
    private String id;

    public Productor(Monitor m, int P, String id) {
        this.m = m;
        this.n = 0;
        this.P = P;
        this.id = id;
    }

    public void run() {
        for (int i = 0; i < P; i++) {
            String id = this.id + "-" + Integer.toString(n);
            n++;
            Producto p = new Producto(id);
            m.producir(p, id); 
        }
    }
}