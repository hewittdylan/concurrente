package concurrencia;

public class LockRompeEmpate {
	private Entero[] in, last;  //Guardamos en last[i] el último proceso que ha llegado al estado i
	private int N = 10000;		//y en in[i] en qué estado está el proceso 
	
	public LockRompeEmpate() {
		last = new Entero[N];
		in = new Entero[N];
		for (int i = 0; i < N; i++) {
			in[i] = new Entero(0);
			last[i] = new Entero(0);
		}
	}
	
	public void takeLock(int i) {
		for (int j = 0; j < N; j++) {
			in[i - 1].setValor(j + 1);
			last[j].setValor(i);
			for (int k = 1; k <= N; k++) {
				if (k == i) continue;
				while (in[k - 1].getValor() >= in[i - 1].getValor() && last[j].getValor() == i) {}
				//Bucle mientras la etapa del proceso k sea mayor o igual a la del proceso i
				//Y el id del último proceso que llegó a la etapa j sea i
			}
			
		}
	}
	
	public void releaseLock(int i) {
		in[i - 1].setValor(0);
	}
}
