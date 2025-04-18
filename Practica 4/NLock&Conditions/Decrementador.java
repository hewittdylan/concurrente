public class Decrementador extends Modificacion {

    public Decrementador(int n, MonitorLC m, int id) {
        super(m, id, n);
    }

    protected void critical() {
        m.decrementar();
    }
}