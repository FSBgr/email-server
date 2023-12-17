

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * @author Christos Christidis, 3350, christpc@csd.auth.gr
 */

public class MultiServerThread extends Thread {

    private final Socket clientSocket;

    private static Account currentUser;

    private final List<Account> users;

    private boolean userConnected = false;
    private final String generalCommand = "Press 'NEW' to write a new email, 'SHOW' to see your emails, 'READ' to read an email, " +
            "'DELETE' to delete an email,'LOGOUT' to logout or 'EXIT' to exit";

    public MultiServerThread(Socket socket, List<Account> users) throws IOException {
        super("MultiServerThread");
        this.clientSocket = socket;
        currentUser = new Account();
        this.users = users;
    }

    /**
     * The method that runs the email server Thread. Prints the menu's based on whether there's a user connected or not.
     * Initial user is stated as guest. Thread runs until the user uses the EXIT feature/function.
     */
    public void run() {

        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine, outputLine;


            outputLine = "Hello guest! Press 1 to login, 2 to register or 3 to exit.";
            out.println(outputLine);

            while (true) {
                if (!userConnected) {       //if it's a guest.
                    while ((inputLine = in.readLine()) != null) {               //reading user's input and deciding the function
                        if (inputLine.equals("1")) {
                            if (!logIn(out, in)) {
                                out.println("Login unsuccessful.##PRESS ANY BUTTON TO CONTINUE");
                                continue;
                            } else {
                                break;
                            }
                        }
                        if (inputLine.equals("2")) {
                            if (!register(out, in)) {
                                out.println("Register unsuccessful.##PRESS ANY BUTTON TO CONTINUE");
                                continue;
                            }
                            break;
                        }
                        if (inputLine.equals("3")) {
                            exit(out, in);
                        } else {
                            outputLine = "Please select 1 to login, 2 to register or 3 to exit.";
                            out.println(outputLine);
                        }
                    }
                }

                in.readLine();                          //filler, does nothing, just helps so the system may proceed.
                outputLine = "#" + generalCommand;
                out.println(outputLine);


                if (userConnected) {    //if there's a user connected
                    while ((inputLine = in.readLine()) != null) {               //reading input from user and deciding the function based on their input
                        if ("EXIT".equalsIgnoreCase(inputLine)) {
                            exit(out, in);
                        } else if ("SHOW".equalsIgnoreCase(inputLine)) {
                            out.println(showEmails());
                        } else if ("NEW".equalsIgnoreCase(inputLine)) {
                            if (newEmail(out, in)) {
                                outputLine = "EMAIL sent successfully!##" + generalCommand;
                            } else {
                                outputLine = "EMAIL wasn't sent. Please try again.##" + generalCommand;
                            }
                            out.println(outputLine);
                        } else if ("READ".equalsIgnoreCase(inputLine)) {
                            if (!readEmail(out, in)) {
                                outputLine = "Your mailbox is empty. No emails to be read.##" + generalCommand;
                                out.println(outputLine);
                            }
                        } else if ("DELETE".equalsIgnoreCase(inputLine)) {
                            if (deleteEmail(out, in)) {
                                outputLine = "Email deleted successfully!##" + generalCommand;
                            } else {
                                outputLine = "Your mailbox is empty. No emails found to be deleted.##" + generalCommand;
                            }
                            out.println(outputLine);
                        } else if ("LOGOUT".equalsIgnoreCase(inputLine)) {
                            logOut();
                            out.println("You've logged out!##PRESS ANY KEY TO CONTINUE");
                            userConnected = false;
                            break;
                        } else {
                            outputLine = "Invalid input.Please try again.##" + generalCommand;
                            out.println(outputLine);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Client has exited.");
        }
    }

    /**
     * This method checks if there's a registered user with the given username.
     *
     * @param username, the username to be inspected.
     * @return the position of the user in the users ArrayList if they exist, otherwise -1.
     */
    public int exists(String username) {
        for (int i = 0; i < users.size(); i++) {
            if (username.equals(users.get(i).getUsername()))
                return i;                                         //Returns the position of the user in the Users list
        }
        return -1;
    }


    /**
     * This method reads a username and a password given by the user and registers them in the server if the username is unique.
     * Otherwise it asks for a re-entry of the username.
     *
     * @param out, the PrintWriter output stream that the socket uses.
     * @param in,  the BufferedReader input stream that the socket uses.
     * @return true if the user was registered properly.
     * @throws IOException
     */
    public boolean register(PrintWriter out, BufferedReader in) throws IOException {
        String outputLine, password, username;
        outputLine = "You chose register, please insert username";
        out.println(outputLine);
        username = in.readLine();
        while ((exists(username) != -1) || (!(username.contains("@") && (username.contains(".com"))))) {      //username must contain '@' and belong to domain .com
            if (username.equalsIgnoreCase("exit"))
                return false;                                               //if user decides that they no longer wish to continue the registry
            out.println("User already exists or invalid username. Please select another username or press 'EXIT' to exit");
            username = in.readLine();
        }
        out.println("Please insert password.");
        password = in.readLine();
        out.println("Register successful! Welcome '" + username + "'!##PRESS ANY KEY TO CONTINUE");
        currentUser = new Account(username, password);
        users.add(currentUser);
        userConnected = true;
        return true;
    }

    /**
     * This method reads a username and a password from the user. In case the username and the password match the username and password
     * of an existing user in the users ArrayList, then the method logs them in. Otherwise it asks a new entry of the invalid credentials
     *
     * @param out, the PrintWriter output stream that the socket uses.
     * @param in,  the BufferedReader input stream that the socket uses.
     * @return true if log in was successful.
     * @throws IOException
     */
    public boolean logIn(PrintWriter out, BufferedReader in) throws IOException {
        String outputLine, password, username;
        outputLine = "You chose login, please insert username";
        out.println(outputLine);
        username = in.readLine();
        while (exists(username) == -1) {                //make sure that there's a user with that name
            if ("EXIT".equalsIgnoreCase(username))
                return false;                           //if user no longer wishes to log in
            if (exists(username) == -1) {
                out.println("User does not exist. Please insert a valid username or type 'EXIT' to exit.");
                username = in.readLine();
            }
        }
        out.println("Please insert password");
        password = in.readLine();
        while (!users.get(exists(username)).getPassword().equals(password)) {                   //checking if password matches
            if (password.equalsIgnoreCase("EXIT"))
                return false;
            out.println("Wrong password. Please try again or press 'EXIT' to exit");
            password = in.readLine();
        }
        userConnected = true;
        currentUser = users.get(exists(username));
        out.println("Login successful! Welcome '" + username + "'!##PRESS ANY KEY TO CONTINUE");
        return true;
    }

    /**
     * This method is used to write a new email. Asks from user to insert a recipient and if the recipient exists in the
     * users array list, then subject and main body are inserted and the email is added in the recipients mailbox. The email's id
     * is calculated upon the recipient's mailbox size.
     *
     * @param out, the PrintWriter output stream that the socket uses.
     * @param in,  the BufferedReader input stream that the socket uses.
     * @return true if the email was sent successfully.
     */
    public boolean newEmail(PrintWriter out, BufferedReader in) {

        try {
            String outputLine;
            outputLine = "You chose NEW EMAIL, please insert the recipient";
            out.println(outputLine);
            String recipient = in.readLine();
            while (exists(recipient) == -1) {           //checking if there's such recipient
                if (recipient.equals("EXIT"))
                    return false;
                outputLine = "This user doesn't exist, please try again. Enter a valid username or press 'EXIT' to leave'";
                out.println(outputLine);
                recipient = in.readLine();

            }
            outputLine = "Please insert SUBJECT";
            out.println(outputLine);
            String subject = in.readLine();
            outputLine = "Please write the main body";
            out.println(outputLine);
            StringBuilder mainBody = new StringBuilder(in.readLine());
            Email email = new Email(true, currentUser.getUsername(), recipient, subject, mainBody, users.get(exists(recipient)).getMailbox().size() + 1); //creating the new email based on the user's inputs
            users.get(exists(recipient)).getMailbox().add(email);           //adding the mail to the recipient's mailbox
        } catch (IOException e) {
            System.err.println("Problem appeared.");
        }
        return true;
    }


    /**
     * This method is used to show the current user's emails. If their mailbox is not empty, a StringBuilder object is
     * created that consists of the header "ID...FROM...SUBJECT" and then the corresponding id, sender and subject strings
     * of each email. Each email preview appears in a new line. If the mailbox is empty, an error message is created instead.
     *
     * @return outputLine, the email previews or error message.
     */
    public StringBuilder showEmails() {
        StringBuilder outputLine;
        String status = "";
        if (currentUser.getMailbox().size() != 0) {
            outputLine = new StringBuilder("ID              FROM                              SUBJECT##");
            for (Email email : currentUser.getMailbox()) {
                if (email.getIsNew())
                    status = "  [NEW]";
                else
                    status = "";
                outputLine.append(email.getId()).append(status).append("      ").append(email.getSender()).append("           "). //building a String that contains a preview of ALL mails within the mailbox.
                        append(email.getSubject()).append("#");
            }
        } else {
            outputLine = new StringBuilder("There are no emails in your mailbox yet");
        }
        outputLine.append("#").append(generalCommand);
        return outputLine;
    }

    /**
     * This method is used to read a particular email. If the current user's mailbox is not empty, it is asked from them to
     * insert an email ID. If the id is valid then the email's information (status, sender and subject) along with its mainbody
     * are printed. Error message is printed otherwise. If the email was stated as NEW, it changes to READ.
     *
     * @param out, the PrintWriter output stream that the socket uses.
     * @param in,  the BufferedReader input stream that the socket uses.
     * @return true if email was shown successfully.
     * @throws IOException
     */
    public boolean readEmail(PrintWriter out, BufferedReader in) throws IOException {
        if (currentUser.getMailbox().size() == 0)
            return false;
        String outputLine = "Please insert the email ID that you wish to read.";
        out.println(outputLine);
        String emailId = in.readLine();
        int id = Integer.parseInt(emailId);
        while (currentUser.findEmail(id) == -1) {
            if (emailId.equals("EXIT"))
                return false;
            outputLine = "This EMAIL doesn't exist, please try again. Enter a valid EMAIL ID or press 'EXIT' to leave'";
            out.println(outputLine);
            emailId = in.readLine();
            id = Integer.parseInt(emailId);
        }
        int emailPosition = currentUser.findEmail(id);
        String emailStatus;
        if (currentUser.getMailbox().get(emailPosition).getIsNew()) {
            emailStatus = "NEW";
            currentUser.getMailbox().get(emailPosition).setIsNew(false);
        } else
            emailStatus = "READ";
        out.println("       STATUS: " + emailStatus + "#" + "       FROM: " + currentUser.getMailbox().get(emailPosition).getSender() + "#       SUBJECT: " + currentUser.getMailbox().get(emailPosition).getSubject() + "#" + "        MAIN BODY: " +
                currentUser.getMailbox().get(emailPosition).getMainbody() +
                "##" + generalCommand); //it will show "NEW" only the first time the user reads it
        return true;
    }

    /**
     * This method is used to delete an email. If the current user's mailbox is not empty then it is asked from them to insert
     * the email's id they wish to delete. If the email id is valid then the email is deleted (removed from their mailbox). Otherwise
     * error message is printed.
     *
     * @param out, the PrintWriter output stream that the socket uses.
     * @param in,  the BufferedReader input stream that the socket uses.
     * @return true if the email was deleted successfully.
     * @throws IOException
     */
    public boolean deleteEmail(PrintWriter out, BufferedReader in) throws IOException {
        if (currentUser.getMailbox().size() == 0) {
            return false;
        }
        String outputLine = "Please insert the email ID that you wish to delete.";
        out.println(outputLine);
        String emailId = in.readLine();
        int id = Integer.parseInt(emailId);
        while (currentUser.findEmail(id) == -1) {
            if (emailId.equals("EXIT"))
                return false;
            outputLine = "This EMAIL doesn't exist, please try again. Enter a valid EMAIL ID or press 'EXIT' to leave'";
            out.println(outputLine);
            emailId = in.readLine();
            id = Integer.parseInt(emailId);
        }
        int email = currentUser.findEmail(id);
        currentUser.getMailbox().remove(email);
        return true;
    }

    /**
     * Resets the current user as a guest.
     */
    public void logOut() {
        currentUser = new Account("Guest");
        userConnected = false;
    }

    /**
     * It frees the resources used (closes I/O streams) and exits the system procedure.
     *
     * @param out, the PrintWriter output stream that the socket uses.
     * @param in,  the BufferedReader input stream that the socket uses.
     * @throws IOException
     */
    public void exit(PrintWriter out, BufferedReader in) throws IOException {
        out.close();
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientSocket.close();
    }
}
