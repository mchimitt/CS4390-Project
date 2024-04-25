import java.io.*;
import java.net.*;

public class Client {
    public static void main(String argv[]) throws Exception {

        String message;
        String response;

        // default ip address and port number
        String ip_addr = "127.0.0.1";
        int port_num = 6789;

        if(argv.length >=2 && argv[0] != null && argv[1] != null) {
            ip_addr = argv[0];
            port_num = Integer.parseInt(argv[1]);
        }


        Socket clientSocket = new Socket(ip_addr, port_num);

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
        
        outToServer.writeBytes("name-" + name + '\n');
        response = inFromServer.readLine();

        // main loop of the client: send message, receive a response.
        while(true) {
            System.out.println("\n");

            System.out.println("Enter \"exit\" to disconnect.\nEnter Math Equations in Prefix Notation.\n(e.g. + 2 3)");
            System.out.print(">> ");

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
                System.out.println("Disconnecting from the Server.\n\nExiting");
                // client socket closes and break out of the while loop to let the program finish.
                clientSocket.close();
                break;
            } 
            // if response is error, that means incorrect prefix
            else if(response.equals("error-0")) {
                System.out.println("Server Found Incorrect Notation.\nFormat should be in prefix notation, e.g. \"+ 1 1\" = 2");
            }

        }
    }
}
