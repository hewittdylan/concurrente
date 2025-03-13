class MultiplicadorFila extends Thread {
    private int[][] A, B, C;
    private int fila, N;

    public MultiplicadorFila(int[][] A, int[][] B, int[][] C, int fila, int N) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.fila = fila;
        this.N = N;
    }

    @Override
    public void run() {
        for (int j = 0; j < N; j++) {
            C[fila][j] = 0;
            for (int k = 0; k < N; k++) {
                C[fila][j] += A[fila][k] * B[k][j];
            }
        }
    }
}

public class practica1C {
    public static void main(String[] args) {
        int N = 3; // Tamaño de la matriz
        int[][] A = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        int[][] B = {{9, 8, 7}, {6, 5, 4}, {3, 2, 1}};
        int[][] C = new int[N][N];

        Thread[] threads = new Thread[N];

        for (int i = 0; i < N; i++) {
            threads[i] = new MultiplicadorFila(A, B, C, i, N);
            threads[i].start();
        }

        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Matriz resultado:");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(C[i][j] + " ");
            }
            System.out.println();
        }
    }
}
