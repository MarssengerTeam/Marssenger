package team.mars.marssenger.datatype;


/**
 * Created by TimoBlock on 24.07.2014.
 */

public class Message {
    private long id;
    private String message;
    private String receiver;
    private String timestamp;
    private String sender;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public String getReciver() {return receiver;}

    public String getTimestamp(){return timestamp;}

    public String getSender(){return  sender;}
    public void setMessage(String message) {
        this.message = message;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return message;
    }

    public void setSender(String sender){this.sender = sender;}

    public void setReciver(String receiver) {
        this.receiver = receiver;
    }

    public void setTime(String time) {
        this.timestamp = time;
    }
}