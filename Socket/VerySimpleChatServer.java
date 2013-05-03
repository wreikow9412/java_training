import java.io.*;
import java.net.*;
import java.util.*;

public class VerySimpleChatServer {

	ArrayList clientOuputStreams;

	public class ClientHandler implements Runnable{
		BufferedReader reader;
		Socket sock;

		public ClientHandler(Socket clientSocket){
			try{
				sock =  clientSocket;
				InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(isReader);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}//constructor

		public void run(){
			String message;
			try{
				while ((message = reader.readLine()) != null){
					System.out.println("read " + message);
					tellEveryone(message);
				}//while
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}//run
	}//class ClinetHandler

	public static void main(String[] args){
		new VerySimpleChatServer().go();
	}

	public void go(){
		clientOuputStreams = new ArrayList();
		try{
			//ServerSocket makes this server application "listen" for client requests on port 5000 on the machine his code is running on
			ServerSocket serverSock = new ServerSocket(5000); //creates a server socket, bound to the specified port
			//The server goes into a permanent loop, waiting ofr (and serving) client request
			while(true){
				//The method blocks(just sits there) until a request comes in, 
				//and then the method returns a Socket(on some anonymous port) for communicating with the client
				Socket clientSocket = serverSock.accept(); 

				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				clientOuputStreams.add(writer);

				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
				System.out.println("got a connection");
			}//while
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}//go

	public void tellEveryone(String message){
		Iterator it = clientOuputStreams.iterator();
		while(it.hasNext()){
			try{
				PrintWriter writer = (PrintWriter)it.next();
				writer.println(message);
				writer.flush();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}//while
	}//tellEveryone
}