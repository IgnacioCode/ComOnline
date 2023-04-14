package comonline;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class User {
	
	private String username;
	private String password;
	private String dirIP;
	private Socket socket;

    // Crea un flujo de salida para enviar el mensaje al servidor
	private OutputStream outputStream;
	private DataOutputStream dataOutputStream;
	
	private InputStream inputStream;
	private DataInputStream dataInputStream;
	
	//private final int CONNECTION_PORT = 60453;
	private final int CONNECTION_PORT = 60450;
	//private final String SERVER_IP_PORT = "192.168.1.2";
	
	public User(String username,String password,String dirIP) throws UnknownHostException, IOException {
		this.username = username;
		this.password = password;
		this.dirIP = dirIP;
		
		socket = new Socket(this.dirIP, CONNECTION_PORT);
		
	    outputStream = socket.getOutputStream();
	    dataOutputStream = new DataOutputStream(outputStream);
	    
	    inputStream = socket.getInputStream();
	    dataInputStream = new DataInputStream(inputStream);
	    
	}
	
	public void enviarMensaje(String mensaje) {
        try {
            // Envía el mensaje al servidor
            dataOutputStream.writeUTF(mensaje);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public void recibirMensajes() {
		new Thread(() -> {
			try {
				while (true) {
					// Lee el mensaje enviado por el servidor
					String mensaje = dataInputStream.readUTF();
					System.out.println("Mensaje recibido del servidor: " + mensaje);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	public void closeConnection() throws IOException {
		
		dataOutputStream.close();
        outputStream.close();
        socket.close();
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		String serverIP = args[0];
		
		
		System.out.println("--------------------------------------");
		System.out.println("Bienvenido al servicio de ComOnline");
		System.out.print("Ingrese su nombre de usuario: ");
		
		try (Scanner scanner = new Scanner(System.in)) {
			
			String username = scanner.nextLine();
			String message;
			System.out.print("Ingrese su contraseña: ");
			String password = scanner.nextLine();
			User user = new User(username, "123",serverIP);
			user.enviarMensaje(username+"\n");
			user.enviarMensaje(password+"\n");
			
			System.out.println("Envie un mensaje:");
			
			while(!(message = scanner.nextLine()).equals("endCon")) {
				user.enviarMensaje(message+"\n");
				System.out.println("Envie otro mensaje:");
			}
			user.enviarMensaje("endCon\n");
			user.closeConnection();
			
		}
		
		// Aquí puedes continuar con la lógica del cliente
	}


}
