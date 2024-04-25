import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Stack;
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

    //Solves the prefix equation give as String
    //Can perform arithmetic operations(+,-,*,/) on integers in prefix notation
    public static int solvePrefixEquation(String input){

        Stack<Integer> stack = new Stack<Integer>();

        //Iterate through the string backwards
        for(int i = input.length()-1; i>=0; i--)
        {
            char currChar = input.charAt(i);

            //Skip the current char if it is a space
            if(currChar == ' ')
                continue;

            //Operator encountered (Not a digit)
            if(!(currChar >= '0' && currChar <= '9')){

                //Perform the given operation with the top two values on the stack
                int op1 = (int)stack.pop();
                int op2 = (int)stack.pop();

                //Switch Case to determine operator
                switch(currChar){
                    case '+':
                        stack.push(op1+op2);
                        break;
                    case '-':
                        stack.push(op1-op2);
                        break;
                    case '*':
                        stack.push(op1*op2);
                        break;
                    case '/':
                        stack.push(op1/op2);
                        break;
                }
                continue;
            }

            //Digit Encountered
            //Number can be greater than 1 digit
            //Variables for calculating
            int total = 0;
            int multiplier = 1;

            //While the current char is a digit (Not a space)
            while(currChar >= '0' && currChar <= '9'|| currChar == '-' ){

                //Negative symbol reached only if there was a digit to the right of it
                if(currChar == '-'){

                    //If negative symbol, make total negative
                    total *= -1;
                    break;
                }
                //Sum the total with the
                //product of the integer value of the current char and the current multiplier(1, 10, 100...)
                total += (int)(currChar-'0') * multiplier;

                //Multiply by 10 for the next digit to the left
                multiplier *= 10;

                //Set the current char to the next on the left
                currChar = input.charAt(--i);
            }

            //Push the value to the stack
            stack.push(total);
        }

        //Return the final value in the stack
        return stack.pop();
    }



    //Checks to see whether a given String is in prefix notation
    //For (+,-,*,/) and integers
    //1 or more spaces must seperate each token
    //Ignores leading and trailing whitespace
    public static boolean isPrefixNotation(String input){

        //Variable to keep track of the number of operands and operators
        int operatorCount=0;
        int operandCount=0;

        //If input is an empty string return false
        if(input.length() == 0)
            return false;

        //Iterate thorugh the string backwards
        int i = input.length()-1;
        char currChar = input.charAt(i);
        while(true){

            //Operator Encountered
            if(currChar == '+' || currChar == '-' || currChar == '*' || currChar == '/'){

                //Increment the operator total
                operatorCount++;

                //If it is the first operator break
                if(i==0){break;}

                //If it is not the first operator move to next left position (Should be a space)
                currChar = input.charAt(--i);
            }

            //Operand Encountered
            else if(currChar >= '0' && currChar <= '9'){

                //While the current char is a number
                //The smallest possible index position for a number is 2
                while(currChar >= '0' && currChar <= '9' && i>=2){

                    //Move to next left position (Should be space or negative sign)
                    currChar = input.charAt(--i);
                }

                //After moved through whole number, check if there is a negative sign
                if(currChar == '-'){

                    //Move to next left position (Should be a space)
                    currChar = input.charAt(--i);

                }

                //Increment the operand total
                operandCount++;
            }

            //If current char is not a space
            //Or if the number of operators exceeds the number of operands
            if(currChar != ' ' || operatorCount >= operandCount && operatorCount > 0)
                return false;

            //For the case of leading zeros
            if(i==0){break;}

            //Move to next left position
            currChar = input.charAt(--i);

        }

        //Should be one more operand then operator for prefix position
        //Should be atleast 1 operator in the function
        return operandCount == operatorCount + 1 && operatorCount > 0;
    }
}
