public class Decrementador extends Modificacion {

    public Decrementador(int n, Monitor m, int id) {
        super(m, id, n);
    }

    protected void critical() {
        m.decrementar();
    }
}