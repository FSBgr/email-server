
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Christos Christidis, 3350, christpc@csd.auth.gr
 */

public class MailClient {

    /**
     * Runs the client's side. Continuously reads messages sent from server and sends back responses.
     *
     * @param args, args[0] = localHost IP address, argos[1] = the port where the Client will be listening.
     * @throws IOException
     */

    public static void main(String[] args) throws IOException {

        Socket clientSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;


        try {
            clientSocket = new Socket(args[0], Integer.parseInt(args[1]));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to host");
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;

        while ((fromServer = in.readLine()) != null) {
            String[] output = fromServer.split("#"); //Instead of \n new line is recognized via # symbol. Whenever # is spotted the printing continuous in a new line.
            for (String s : output) System.out.println(s);

            fromUser = stdIn.readLine();
            if (fromUser != null) {
                System.out.println("Client: " + fromUser);
                out.println(fromUser);
            }
        }

        out.close();
        in.close();
        stdIn.close();
        clientSocket.close();
    }

}
