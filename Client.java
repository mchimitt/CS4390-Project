import java.io.*;
import java.net.*;

public class Client {
    public static void main(String argv[]) throws Exception {

        String message;
        String response;

        Socket clientSocket = new Socket("127.0.0.1", 6789);

        // creating the streams

        // user input, receives the terminal input
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        // stream to send data to the server
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        // stream to receive data from the server.
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.print("Enter Name: ");
        String name = userInput.readLine();
        System.out.println("\n");

        System.out.println("Connected to Server.");
        System.out.println("Enter \"exit\" to disconnect.");
        
        outToServer.writeBytes("name-" + name + '\n');
        response = inFromServer.readLine();

        // main loop of the client: send message, receive a response.
        while(true) {

            // getting user input
            message = userInput.readLine();
            
            // sending the user input to the server.
            outToServer.writeBytes(message + '\n');

            // receiving a response from the server
            response = inFromServer.readLine();

            // Printing the response from the server
            System.out.println("From Server: " + response);

            // managing responses from the server
            response = response.toLowerCase();

            // exit response
            if(response.equals("exit")) {
                // disconnect from the server
                System.out.println("Disconnecting from the Server.\nExiting.");
                // client socket closes and break out of the while loop to let the program finish.
                clientSocket.close();
                break;
            }

        }
    }
}