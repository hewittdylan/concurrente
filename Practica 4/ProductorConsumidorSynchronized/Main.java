public class Main {
    public static final int N = 3;  //Número de productores
    public static final int M = 2;  //Número de consumidores
    public static final int P = 10;
    public static final int C = 15;
    public static final int numProductos = 10;

    public static void main(String[] args) {
        Productor[] prod = new Productor[N];
        Consumidor[] cons = new Consumidor[M];
        Monitor m = new Monitor(numProductos);

        try {
            for (int i = 0; i < N; i++) {
                prod[i] = new Productor(m, P, Integer.toString(i));
            }
            for (int i = 0; i < M; i++) {
                cons[i] = new Consumidor(m, C, Integer.toString(i));
            }
            for (int i = 0; i < N; i++) prod[i].start();
            for (int i = 0; i < M; i++) cons[i].start();
            for (int i = 0; i < N; i++) prod[i].join();
            for (int i = 0; i < M; i++) cons[i].join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
}