package concurrencia;

public class LockBakery extends Lock {
	private Entero[] turnos;
	
	public LockBakery() {
		super();
		turnos = new Entero[N];
		for (int i = 0; i < N; i++) turnos[i] = new Entero(0);
	}
	
	public LockBakery(int N) {
		super(N);
		turnos = new Entero[N];
		for (int i = 0; i < N; i++) turnos[i] = new Entero(0);
	}

	private int maxs(Entero[] array) {
        int max = -1;
        for (int i = 0; i < N; i++) {
            if (max < array[i].getValor()) {
                max = array[i].getValor();
            }
        }
        return max;
    }

    private boolean operator(Entero turno1, int i, Entero turno2, int j) {
        if (turno2.getValor() < turno1.getValor()) return true;
        return turno1.getValor() == turno2.getValor() && j < i;
    }

    @Override
    public void takeLock(int i) {
        turnos[i].setValor(1);
        turnos[i].setValor(maxs(turnos) + 1);

        for (int j = 0; j < N; j++) {
            if (j == i) continue;
            while (turnos[j].getValor() != 0 && operator(turnos[i], i, turnos[j], j)) {}
        }
    }

    @Override
    public void releaseLock(int i) {
        turnos[i].setValor(0);
    }
}
