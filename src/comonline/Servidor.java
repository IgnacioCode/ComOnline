package comonline;

import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class Servidor {
    
	private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int lastPort;
    private Map<String, String> users;
    private final String USERS_FILE = "usersInfo.txt";
    
    public void start(int puerto) throws InterruptedException, IOException {
    	boolean restart = false;
    	
    	users = loadUsers(USERS_FILE);
    	
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
            int numMessages = 0;
            String username = null;
            String password = null;
            
            /*
             * Espera a leer los 2 primeros mensajes con el usuario y contraseña
             */
            while(numMessages<2) {
            	if((mensaje = in.readLine().trim())!= null) {
            		if(numMessages==0) {
            			username = mensaje;
            		}
            		else {
            			password = mensaje;
            		}
            		numMessages++;
            	}
            	
            }
            
            /*
             * Valida las credenciales de usuario, si el usuario no existe lo crea, si existe verifica que este bien la contraseña
             */
            if(!validateUser(username,password)) {
            	if(!users.containsKey(username)) {
            		createNewUser(username,password);
            		out.println("New user created");
            	}
            	else {
            		out.println("User &/or password incorrect"+ "\n");
            		return;
            	}
            	
            }
            else
            	out.println("User checked in");
            
            
            while (!conectionFinished) {
            	
                while ((mensaje = in.readLine().trim()) == null) {
                    System.out.println("No reading from message");
                    Thread.sleep(50000);
                }
                
                if (mensaje.equals("endCon")) {
                    System.out.println("End message recieved");
                    out.println("endCon");
                    conectionFinished = true;
                    return;
                }
                
                System.out.println("Message recieved: " + mensaje);
                System.out.println(mensaje.length());
                out.println("Message sent"+ "\n");
                
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
                saveUsers(USERS_FILE,users);
                if(restart)
                	this.start(lastPort);
                
            } catch (IOException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    public static Map<String, String> loadUsers(String filename) throws IOException {
        Map<String, String> users = new HashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();

        while (line != null) {
            String[] parts = line.split(":");
            String username = parts[0];
            String password = parts[1];

            users.put(username, password);
            line = reader.readLine();
        }

        reader.close();
        return users;
    }
    
    public static void saveUsers(String filename, Map<String, String> users) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (Map.Entry<String, String> entry : users.entrySet()) {
            String line = entry.getKey() + ":" + entry.getValue();
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }
    
    private void createNewUser(String username, String password) {
		users.put(username, password);
	}

	private boolean validateUser(String username, String password) {
    	return users.containsKey(username) && users.get(username).equals(password);
	}

	public static void main(String[] args) throws NumberFormatException, InterruptedException, IOException {
		Servidor server = new Servidor();
		server.start(Integer.parseInt(args[0]));
	}
}
