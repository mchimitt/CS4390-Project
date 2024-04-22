import java.io.*;
import java.net.*;

public class Server {
    public static void main(String argv[]) throws Exception
    {

        int id = 1;

        // creating the welcome socket
        ServerSocket welcomeSocket = new ServerSocket(6789);

        System.out.println("\nServer is Running\n");

        // loop to accept new connections
        while(true) {
            // three way handshake: we wait for a client to accept the welcome socket, 
            // then we create a new socket for the connection
            Socket connectionSocket = welcomeSocket.accept();
            // creating a thread for each connection and passing in the socket
            // this is done in order for the server to service many clients at once
            // a thread per connection.
            ServerThread serverThread = new ServerThread(connectionSocket, id);
            id++;
        }
    }
}


class ServerThread extends Thread {

    Socket socket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;

    String clientName = "";
    
    String clientMessage;
    
    public ServerThread(Socket socket, int id) throws Exception {
        this.socket = socket;

        // default name if there is not given name.
        clientName = "client_" + id;
        
        // stream to receive data from the client
        inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // stream to send data to the client
        outToClient = new DataOutputStream(socket.getOutputStream());

        // starting the thread.
        this.start();
    }

    @Override
    public void run() {
        // where the loop is stored
        try {  
            // receive a message, send response
            while((clientMessage = inFromClient.readLine()) != null) { 
                
                clientMessage = clientMessage.toLowerCase();

                // Checks if the message that is sent is the message indicating the client name.
                if(clientMessage.substring(0, 4).equals("name")) {
                    // whatever comes after "name-" is the client name
                
                    // if the name is left empty, then we give it a default name.
                    if(clientMessage.substring(5).length()>0 && !clientMessage.substring(5).equals(" ")) 
                        clientName = clientMessage.substring(5);

                    System.out.println("Connected to Client: " + clientName);
                }
                
                // handling an exit
                else if(clientMessage.equals("exit")) {
                    // send an "exit" message back to the client
                    outToClient.writeBytes("exit");
                    // disconnect from the client
                    System.out.println("Disconnecting from Client: " + clientName + "\n");
                    inFromClient.close();
                    outToClient.close();
                    socket.close();
                    // break out of the loop to let the thread finish.
                    break;
                } else {
                    // printing the message received from the client
                    // FOR DEBUGGING - GET RID OF THIS LATER
                    System.out.println("RECEIVED FROM " + clientName + ": " + clientMessage);
                }

                // Creating the response message
                String responseMessage = "SERVER RETURNING " + clientMessage + '\n';
                // sending the response message.
                outToClient.writeBytes(responseMessage);
            }

        // Catch the exception
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            // Close the streams and sockets at the end.
            try {
                inFromClient.close();
                outToClient.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
