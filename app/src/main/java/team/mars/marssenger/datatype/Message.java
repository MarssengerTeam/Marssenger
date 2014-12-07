package team.mars.marssenger.datatype;


/**
 * Created by TimoBlock on 24.07.2014.
 */

public class Message {
    private long id;
    private String message;
    private String receiver;
    private long timestamp;
    private boolean sender;
    private boolean read;
    private long chatID;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setChatID(long id){
        this.chatID=id;
    }
    public long getChatID(){
        return chatID;
    }
    public boolean isRead(){
        return read;
    }
    public void setRead(int read){
        if(read==1){
            this.read=true;
        }
        else {
            this.read=false;
        }

    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp(){return timestamp;}

    public boolean isSender(){return  sender;}
    public void setMessage(String message) {
        this.message = message;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return message;
    }

    public void setSender(int sender){
        if(sender ==1){
        this.sender=true;}else{
        this.sender=false;
        }
    }


    public void setTime(long time) {
        this.timestamp = time;
    }
}