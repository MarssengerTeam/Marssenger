package team.mars.marssenger.datatype;


import android.util.Log;

import java.io.Serializable;

/**
 * Created by TimoBlock on 24.07.2014.
 */

public class Message implements Serializable {
    private int type;
    private long id;
    private Object message;
    private String receiver;
    private long timestamp;
    private boolean sender;
    private boolean read;
    private long chatID;

    Message(){}
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
        this.read = read == 1;

    }

    public Object getMessage() {
        return message;
    }

    public long getTimestamp(){return timestamp;}

    public boolean isSender(){return  sender;}
    public void setMessage(String message) {
        this.message = message;
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
    public int getType(){return type;}
    public void debug(){
        Log.d("MESSAGEDEBUG","ID:"+id+"\n"+"CID:"+chatID+"\nREAD:"+read+"\nMSG:"+message+"\nisSender:"+sender+"\nTIME:"+timestamp+"\nTYPE:"+type);
    }
}