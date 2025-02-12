class MiThread extends Thread {
    private int id;
    private int tiempoEspera;

    public MiThread(int id, int tiempoEspera) {
        this.id = id;
        this.tiempoEspera = tiempoEspera;
    }

    @Override
    public void run() {
        System.out.println("Inicio del thread " + id);
        try {
            Thread.sleep(tiempoEspera);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Fin del thread " + id);
    }
}

public class practica1A {
    public static void main(String[] args) {
        int N = 5; // Número de threads
        int[] tiempos = {1000, 2000, 1500, 2500, 3000}; // Distintos tiempos de espera

        Thread[] threads = new Thread[N];

        for (int i = 0; i < N; i++) {
            threads[i] = new MiThread(i, tiempos[i]);
            threads[i].start();
        }

        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Todos los threads han terminado.");
    }
}