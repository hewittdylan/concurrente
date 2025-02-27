class LockTicket {
    private volatile int numero = 0;
    private volatile int siguiente = 0;
    private volatile int[] turno;

    public LockTicket(int M) {
        turno = new int[2 * M];
        for (int i = 0; i < 2 * M; i++) {
            turno[i] = 0;
        }
    }

    public void takeLock(int id) {
        turno[id] = numero++;
        while(turno[id] != siguiente) { 
            //Espera activa
        }
    }

    public void releaseLock() {
        siguiente++;
    }

}

class LockBakery {
    private volatile boolean[] eligiendo;
    private volatile int[] numero;

    public LockBakery(int M) {
        eligiendo = new boolean[2 * M];
        numero = new int[2 * M];
        for (int i = 0; i < 2 * M; i++) {
            eligiendo[i] = false;
            numero[i] = 0;
        }
    }

    public void takeLock(int id) {
        eligiendo[id] = true;
        numero[id] = 1 + max(numero);
         
    }
}