

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christos Christidis, 3350, christpc@csd.auth.gr
 */

public class MailServer {


    /**
     * Runs server's side. Some example Account objects are inserted. Server runs a new thread everytime a Client connects.
     *
     * @param args, args[0] = server's port
     * @throws IOException
     */
    public static void main(String[] args) {
        try {

            ServerSocket serverSocket = null;
            boolean listening = true;
            List<Account> users = new ArrayList<Account>();



            /*--------------Initializing random example users and their emails ------------------------*/

            users.add(new Account("example1@email.com", "12345"));
            users.add(new Account("example2@email.com", "12345"));

            users.get(0).getMailbox().add(new Email(true, "unknownemail@email.com", users.get(0).getUsername(), "What's up", new StringBuilder("Long time no see! How are you?"), 1));
            users.get(0).getMailbox().add(new Email(true, "supplier@email.com", users.get(0).getUsername(), "Transaction", new StringBuilder("I'm sending the current email to " +
                    "inform you that there's still a large amount of money owed towards me. Please take care of it ASAP!"), 2));
            users.get(0).getMailbox().add(new Email(true, "gamingcompany@email.com", users.get(0).getUsername(), "Advertisement", new StringBuilder("Try our new game now! Free to play! Click here "), 3));

            users.get(1).getMailbox().add(new Email(true, "example1@email.com", users.get(0).getUsername(), "Basketball", new StringBuilder("Lakers beat the Rockets in" +
                    "a very close call game. It was a real blast, wish you could see it!"), 1));
            users.get(1).getMailbox().add(new Email(true, "governmentemail@email.com", users.get(0).getUsername(), "Tax Evasion", new StringBuilder("There has been a confirmed" +
                    "case of tax evasion from your part. We are investigating it."), 2));
            users.get(1).getMailbox().add(new Email(true, "accounting@email.com", users.get(0).getUsername(), "You've got money!", new StringBuilder("You've received $500 from Nick Manson. Click here to view."), 3));
            /*-----------------------------------------------------------------------------------------------*/


            try {
                serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            } catch (IOException e) {
                System.err.println("Could not listen on port: " + args[0]);
                System.exit(-1);
            }

            while (listening) {
                Thread thread = new MultiServerThread(serverSocket.accept(), users);
                thread.start();
            }

            serverSocket.close();
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Client socket closed.");
        }
    }

}
