import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private String nombre;
    private List<SocketThread> clientes;

    public static void main(String[] args) {
        try {
            Cliente c = new Cliente(args[0]);
            c.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cliente(String nombre) {
        this.nombre = nombre;
        this.clientes = new ArrayList<>();
    }

    public void start() {
        try {
            Socket socket = new Socket("localhost", 4321);
            System.out.println("Conectado al cliente " + nombre);
            SocketThread socketT = new SocketThread(nombre, socket);
            socketT.start();
            clientes.add(socketT);
            for (SocketThread st : clientes) {
                try {
                    st.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class SocketThread extends Thread {
        private Socket socket;
        private String nombre;
        private ObjectInput input;
        private ObjectOutput output;

        public SocketThread(String nombre, Socket socket) {
            this.nombre = nombre;
            this.socket = socket;
            try {
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                //Fase de conexión
                Mensaje m = new Mensaje(TMensaje.Conexion, "Solicitud de conexión del cliente: " + nombre);
                output.writeObject(m); output.flush();

                //Fase de confirmación
                m = (Mensaje) input.readObject();
                if (m.getTipo() != TMensaje.Confirmacion) throw new Exception("Confirmación de conexión no recibida");
                System.out.println("Confirmación de conexión: " + m.getContenido());

                //Fase de cierre
                m = new Mensaje(TMensaje.Cierre, "Desconexión del cliente: " + nombre);
                output.writeObject(m); output.flush();

                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}