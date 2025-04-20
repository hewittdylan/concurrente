import java.io.Serializable;

enum TMensaje {
    Conexion,
    Confirmacion,
    Cierre
}

class Mensaje implements Serializable {
    private TMensaje tipo;
    private String contenido;

    public Mensaje(TMensaje tipo, String contenido) {
        this.tipo = tipo;
        this.contenido = contenido;
    }

    public TMensaje getTipo() {
        return tipo;
    }

    public String getContenido() {
        return contenido;
    }
} 