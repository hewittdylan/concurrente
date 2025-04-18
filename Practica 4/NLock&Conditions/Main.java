public class Main {
    public static final int N = 100;  //Número de incrementos
    public static final int M = 25;  //Número de hilos para cada tipo

    public static void main(String[] args) {
        Modificacion[] mod = new Modificacion[2 * M];
        MonitorLC m = new MonitorLC();

        try {
            for (int i = 0; i < M; i++) {
                mod[i] = new Incrementador(N, m, i);
                mod[M + i] = new Decrementador(N, m, i);
            }
            for (int i = 0; i < 2 * M; i++) mod[i].start();
            for (int i = 0; i < 2 * M; i++) mod[i].join();
            System.out.println("Shared: " + m.getEntero().getValor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}