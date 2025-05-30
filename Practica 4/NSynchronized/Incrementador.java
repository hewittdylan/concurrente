public class Incrementador extends Modificacion {
    
    public Incrementador(int n, Monitor m, int id) {
        super(m, id, n);
    }

    protected void critical() {
        m.incrementar();
    }
}