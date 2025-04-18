public class Monitor {
    private Producto[] productos;
    private Entero ini;
    private Entero fin;
    private Entero contador;

    public Monitor(int n) {
        productos = new Producto[n];
        for (int i = 0; i < n; i++) productos[i] = new Producto("-1");
        ini = new Entero(0);
        fin = new Entero(0);
        contador = new Entero(0);
    }

    public synchronized void producir(Producto p, String id) {
        //Mientras esté lleno, espera
        while (contador.getValor() == productos.length) {
            try {
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        productos[fin.getValor()] = p;
        System.out.println("Almacenado el producto: " + p.getId());
        fin.incrementar();  //Incrementamos y aplicamos el módulo
        if (fin.getValor() == productos.length) fin.setValor(0);
        contador.incrementar();
        notifyAll();
    }

    public synchronized  void consumir() {
        //Mientras no haya nada que consumir
        while (contador.getValor() == 0) {
            try {
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Consumimos el producto: " + productos[ini.getValor()].getId());
        ini.incrementar();  //Incrementamos y aplicamos el módulo
        if (ini.getValor() == productos.length) ini.setValor(0);
        contador.decrementar();
        notifyAll();
    }
}