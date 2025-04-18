import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
    private Producto[] productos;
    private Entero ini;
    private Entero fin;
    private Entero contador;
    private final Lock lock;
    private final Condition vacio;
    private final Condition lleno;

    public Monitor(int n) {
        productos = new Producto[n];
        for (int i = 0; i < n; i++) productos[i] = new Producto("-1");
        ini = new Entero(0);
        fin = new Entero(0);
        contador = new Entero(0);
        lock = new ReentrantLock();
        vacio = lock.newCondition();
        lleno = lock.newCondition();
    }

    public void producir(Producto p, String id) {
        lock.lock();
        //Mientras esté lleno, espera
        while (contador.getValor() == productos.length) {
            try {
                vacio.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        productos[fin.getValor()] = p;
        System.out.println("Almacenado el producto: " + p.getId());
        fin.incrementar();  //Incrementamos y aplicamos el módulo
        if (fin.getValor() == productos.length) fin.setValor(0);
        contador.incrementar();
        lleno.signal();
        lock.unlock();
    }

    public void consumir() {
        lock.lock();
        //Mientras no haya nada que consumir
        while (contador.getValor() == 0) {
            try {
                lleno.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Consumimos el producto: " + productos[ini.getValor()].getId());
        ini.incrementar();  //Incrementamos y aplicamos el módulo
        if (ini.getValor() == productos.length) ini.setValor(0);
        contador.decrementar();
        vacio.signal();
        lock.unlock();
    }
}