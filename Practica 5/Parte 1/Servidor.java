import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    private ServerSocket serverSocket;
    private List<SocketThread> clientes;

    public static void main(String[] args) {
        try {
            Servidor servidor = new Servidor();
            servidor.start();
            servidor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Servidor() throws IOException {
        serverSocket = new ServerSocket(4321);
        clientes = new ArrayList<>();
    }

    public void start() {
        int n = 0;
        while (n < 5) {
            try {
                Socket socket = serverSocket.accept();
                SocketThread st = new SocketThread(socket);
                st.start();
                clientes.add(st);
                n++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        for (SocketThread st : clientes) {
            try {
                st.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static class SocketThread extends Thread {
        private Socket socket;
        private ObjectInputStream input;
        private ObjectOutputStream output;

        public SocketThread(Socket socket) {
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
                Mensaje m = (Mensaje) input.readObject();
                if (m.getTipo() != TMensaje.Conexion) throw new Exception("No se ha recibido mensaje de conexión");
                System.out.println("El servidor ha recibido: " + m.getContenido());

                //Fase de confirmación
                m = new Mensaje(TMensaje.Confirmacion, "Confirmación de conexión");
                output.writeObject(m); output.flush();

                //Fase de cierre
                m = (Mensaje) input.readObject();
                if (m.getTipo() != TMensaje.Cierre) throw new Exception("No se ha recibido un mensaje de cierre");
                System.out.println("El servidor ha recibido: " + m.getContenido());

                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}