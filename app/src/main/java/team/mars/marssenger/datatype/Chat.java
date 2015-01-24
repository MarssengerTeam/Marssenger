package team.mars.marssenger.datatype;


import java.io.Serializable;

public class Chat implements Serializable{

    /*
    all the fields you need

    id : long
    messageTableId : long
    name : String
    receivers : String []
    type : boolean
     */

    //KEYS
    public static final String CHAT="chat";

    //default const
    public Chat(){}

    //const with everything
    public Chat(
            long id,
            long messageTableId,
            String name,
            String receiver,
            boolean type
    ){
        this.id=id;
        this.messageTableId=messageTableId;
        this.name=name;
        this.receiver=receiver;
        this.type=type;
    }

    long id;
    long messageTableId;
    String name;
    String receiver;
    boolean type;

    public boolean isSingleChat(){return type;}

    public long getId(){return id;}
    public void setId(long id){this.id=id;}


    public String getName(){return name;}
    public void setName(String name){this.name=name;}

    // Will be used by the ArrayAdapter in the ListView
    //public String toString();

    public void setMessageTableId(long messageTableId){this.messageTableId=messageTableId;}
    public long getMessageTableId(){return messageTableId;}

    public void setReceiver(String receiver){this.receiver=receiver;}
    public String getReceiver(){return receiver;}
}