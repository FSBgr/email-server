
/**
 * Class that represents an email.
 *
 * @author Christos Christidis, 3350, christpc@csd.auth.gr
 */

public class Email {

    private boolean isNew;
    private String sender;
    private String receiver;
    private String subject;
    private StringBuilder mainBody;
    private int id;


    public Email(boolean isNew, String sender, String receiver, String subject, StringBuilder mainBody, int id) {
        this.isNew = isNew;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.mainBody = mainBody;
        this.id = id;
    }


    public boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(boolean aNew) {
        isNew = aNew;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public StringBuilder getMainbody() {
        return mainBody;
    }

    public void setMainBody(StringBuilder mainbody) {
        this.mainBody = mainbody;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
