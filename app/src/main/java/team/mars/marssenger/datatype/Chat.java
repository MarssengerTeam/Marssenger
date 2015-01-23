package team.mars.marssenger.datatype;


public class Chat {

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
    public static final String CHAT_ID="chatid";
    public static final String CHAT_MESSAGE_TABLE_ID="chatmessagetableid";
    public static final String CHAT_NAME="chatname";
    public static final String CHAT_TYPE="chattype";
    public static final String CHAT_RECEIVER_COUNT="chatreceivercount";

    //default const
    public Chat(){}

    long id;
    long messageTableId;
    String name;
    String [] receiver;
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



    public void setReceivers(String[] receiver){this.receiver=receiver;}
    public String[] getReceiver(){return receiver;}
    public int getReceiverCount(){
        if (receiver==null){
            return 0;
        } else {
            return receiver.length;
        }
    }
}