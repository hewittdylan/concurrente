class LockRompeEmpate {
    private volatile boolean[] flag = {false, false};
    private volatile int turno;

    public void takeLock(int id) {
        int otro = 1 - id;
        flag[id] = true;  //Marco como usando
        turno = id;
        while (flag[otro] && turno == id) {
            //Espera activa
        }
    }

    public void releaseLock(int id) {  //Marco que dejo de usarlo
        flag[id] = false;
    }
}

class Contador {
    public int valor = 0;
}