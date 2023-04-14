package comonline;

import java.net.*;
import java.io.*;

public class Servidor {
    
	private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int lastPort;
    
    public void start(int puerto) throws InterruptedException {
    	boolean restart = false;
        try {
        	lastPort = puerto;
        	boolean conectionFinished = false;
        	
            serverSocket = new ServerSocket(puerto);
            System.out.println("Servidor iniciado en el puerto " + puerto);

            // Esperando conexiones entrantes
            clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostName());

            // Estableciendo el canal de entrada y salida
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Escuchando al cliente
            String mensaje;
            while (!conectionFinished) {
                //mensaje = in.readLine();
                while ((mensaje = in.readLine().trim()) == null) {
                	
                    System.out.println("No reading from message");
                    Thread.sleep(50000);
                    
                }
                if (mensaje.equals("endCon")) {
                	System.out.println("Recibio mensaje finalizacion");
                    conectionFinished = true;
                    return;
                } else {
                    System.out.println("Mensaje recibido: " + mensaje);
                    System.out.println(mensaje.length());
                    out.println("Mensaje enviado");
                }
                if (in.ready() == false) {
                    if (clientSocket.isClosed()) {
                        conectionFinished = true;
                    }
                }
            }

        } catch (IOException e) {
        	if(e.getMessage()=="Connection reset") {
        		restart = true;
        	}
            System.out.println("Error en el servidor: " + e.getMessage());
            
        } finally {
            try {
                // Cerrando la conexión
                in.close();
                out.close();
                clientSocket.close();
                serverSocket.close();
                if(restart)
                	this.start(lastPort);
                
            } catch (IOException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) throws NumberFormatException, InterruptedException {
		Servidor server = new Servidor();
		server.start(Integer.parseInt(args[0]));
	}
}
