package mensajes;

import java.io.Serializable;

public abstract class Mensaje implements Serializable {
	private static final long serialVersionUID = 1L;
	protected TMensaje tipo;
	
	public Mensaje(TMensaje tipo) {
		this.tipo = tipo;
	}
	
	public TMensaje getTipo() {
		return tipo;
	}
}
