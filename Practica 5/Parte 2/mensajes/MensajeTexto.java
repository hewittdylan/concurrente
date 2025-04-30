package mensajes;

public class MensajeTexto extends Mensaje {
	private static final long serialVersionUID = 1L;
	private String texto;
	
	public MensajeTexto(TMensaje tipo, String s) {
		super(tipo);
		this.texto = s;
	}
	
	public String getContenido() {
		return texto;
	}
}
