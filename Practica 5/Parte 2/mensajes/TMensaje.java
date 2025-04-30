package mensajes;

public enum TMensaje {
	M_CONEXION,  //Envía el nombre de usuario
	M_CONFIRMACION_CONEXION,  //Envia el puerto que se le asigna al cliente
	M_LISTA_USUARIOS,
	M_CONFIRMACION_LISTA_USUARIOS,  //Envia la lista de usuarios
	M_PEDIR_FICHERO,  //Envía el nombre del fichero
	M_FICHERO_INEXISTENTE,
	M_INICIO_EMISION,
	M_FIN_EMISION,  //Envia el nombre del fichero
	M_PREPARADO_CS, //Cliente-Servidor Envia nombre del fichero, dirección ip y puerto
	M_PREPARADO_SC, //Servidor-Cliente Envia dirección y puerto del emisor
	M_DESCONECTAR
}