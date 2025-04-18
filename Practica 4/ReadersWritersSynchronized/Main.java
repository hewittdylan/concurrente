public class Main {
    public static final int N = 3;  //Número de productores
    public static final int M = 2;  //Número de consumidores
    public static final int P = 10;
    public static final int C = 15;
    public static final int numProductos = 5;

    public static void main(String[] args) {
        Escritor[] escritores = new Escritor[N];
        Lector[] lectores = new Lector[M];
        Monitor m = new Monitor(numProductos);
        Producto[] productos = new Producto[numProductos];

        for (int i = 0; i < numProductos; i++) productos[i] = new Producto("-1");

        try {
            for (int i = 0; i < N; i++) {
                escritores[i] = new Escritor(m, P, Integer.toString(i), productos);
            }
            for (int i = 0; i < M; i++) {
                lectores[i] = new Lector(m, productos, C);
            }
            for (int i = 0; i < N; i++) escritores[i].start();
            for (int i = 0; i < M; i++) lectores[i].start();
            for (int i = 0; i < N; i++) escritores[i].join();
            for (int i = 0; i < M; i++) lectores[i].join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
}