import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Server {
    public static void main(String argv[]) throws Exception
    {

        int port_num = 6789;

        // Specify port number. if no port number is specified, then uses default of 6789.
        if(argv.length > 0 && argv[0] != null) {
            port_num = Integer.parseInt(argv[0]);
        }


        int id = 1;

        // creating the welcome socket
        ServerSocket welcomeSocket = new ServerSocket(port_num);

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

    // Obj to get the time
    LocalTime endTimeObj;
    LocalTime startTimeObj;

    // duration variables to calculate how long the client was connected.
    long startDuration;
    long endDuration;

    // Strings for the start and stop time.
    String startTime;
    String endTime;

    // formatter for the dates
    DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");

    String clientName = "";
    
    String clientMessage;

    // keeps track of our log file
    String log = "";
    
    public ServerThread(Socket socket, int id) throws Exception {
        this.socket = socket;

        // default name if there is not given name.
        clientName = "client_" + id;
        
        // stream to receive data from the client
        inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // stream to send data to the client
        outToClient = new DataOutputStream(socket.getOutputStream());

        // getting the current time and formatting it correctly
        startTimeObj = LocalTime.now();
        startTime = startTimeObj.format(format);

        // starting the client duration "timer"
        startDuration = System.currentTimeMillis();
        startDuration = TimeUnit.MILLISECONDS.toSeconds(startDuration);

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

                    String connected = "Connected to Client: " + clientName;
                    System.out.println(connected);
                    AddLineToLog(connected);
                }
                
                // handling an exit
                else if(clientMessage.equals("exit")) {
                    
                    // send an "exit" message back to the client
                    outToClient.writeBytes("exit");

                    AddToLog(clientMessage, "exit");

                    // Determining the end time in order to display the length of the connection
                    endDuration = System.currentTimeMillis();
                    endDuration = TimeUnit.MILLISECONDS.toSeconds(endDuration);
                    
                    // getting the time at termination
                    endTimeObj = LocalTime.now();
                    endTime = endTimeObj.format(format);

                    // disconnect from the client
                    String disconnect = "\nDisconnecting from Client: " + clientName + " : Connected for " + (endDuration - startDuration) + " seconds\nConnection began at " + startTime + " and terminated at " + endTime;
                    System.out.println(disconnect + "\n");
                    AddLineToLog(disconnect);
                    

                    // download the log file!
                    File file = new File(clientName + "_log");
                    FileWriter fw = new FileWriter(file);
                    fw.write(log);
                    fw.close();
                    
                    System.out.println(clientName + "_log.txt file saved to directory.\n");

                    // closing connections
                    inFromClient.close();
                    outToClient.close();
                    socket.close();

                    // break out of the loop to let the thread finish.
                    break;

                } else {
                    // printing the message received from the client
                    // FOR DEBUGGING - GET RID OF THIS LATER
                    System.out.println(clientName + ": " + clientMessage);
                    



                    // REPLACE THIS WITH THE MATH PROTOCOL STUFF!!

                    

                }

                // Creating the response message
                String responseMessage = clientMessage + '\n';
                // sending the response message.
                outToClient.writeBytes(responseMessage);

                // Logging
                AddToLog(clientMessage, responseMessage);
                
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


    // Adds the request and response to the log
    public void AddToLog(String request, String response) {
        String formattedRequest = "Request From " + clientName + ":  " + request + "\n";
        String formattedResponse = "Server Response:  " + response + "\n";

        log = log + (formattedRequest);
        log = log + (formattedResponse);

    }    

    // adds a single line to the log file
    public void AddLineToLog(String line) {
        log = log + (line + "\n");
    }

}
