
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a user's account in the email server.
 *
 * @author Christos Christidis, 3350, christpc@csd.auth.gr
 */
public class Account {

    private String username;
    private String password;
    private List<Email> mailbox;

    public Account() {
        username = "Guest";
        password = "---";
        mailbox = new ArrayList<Email>();
    }

    public Account(String name) {
        username = name;
        password = "----";
        mailbox = new ArrayList<Email>();
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        mailbox = new ArrayList<Email>();
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Email> getMailbox() {
        return mailbox;
    }

    public void setMailbox(List<Email> mailbox) {
        this.mailbox = mailbox;
    }

    public void addMail(Email email) {
        mailbox.add(email);
    }

    /**
     * Searches for an email in the account's mailbox.
     *
     * @param id, the email's id.
     * @return the position of the email in the mailbox, -1 otherwise.
     */
    public int findEmail(int id) {
        for (int i = 0; i < mailbox.size(); i++) {
            if (mailbox.get(i).getId() == id)
                return i;                       // Returning the position of the email in the mailbox.
        }
        return -1;

    }
}
