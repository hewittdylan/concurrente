package mensajes;

import servidor.Usuario;

public class MensajeUsuario extends Mensaje {
	private static final long serialVersionUID = 1L;
	private Usuario usuario;
	
	public MensajeUsuario(TMensaje tipo, Usuario usuario) {
		super(tipo);
		this.usuario = usuario;
	}
	
	public Usuario getContenido() {
		return usuario;
	}
}
