public class Consumidor extends Thread {
    private Monitor m;
    private int C;
    private String id;

    public Consumidor(Monitor m, int C, String id) {
        this.m = m;
        this.C = C;
        this.id = id;
    }

    public void run() {
        for (int i = 0; i < C; i++) m.consumir();
    }
}