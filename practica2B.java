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