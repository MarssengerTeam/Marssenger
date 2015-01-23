package team.mars.marssenger.datatype;

import java.io.Serializable;

/**
 * Created by Kern on 28.12.2014.
 */

public class SingleChat extends Chat implements Serializable{

    /*
    all the fields you need

    id : long
    messageTableId : long
    name : String
    receivers : String []
    type : boolean
     */

    //default const
    public SingleChat(){}

    @Override
    public boolean isSingleChat() {
        return true;
    }

/*
    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id=id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name=name;
    }

    @Override
    public void setMessageTableId(long id) {
        this.messageTableID=id;
    }

    @Override
    public long getMessageTableId() {
        return messageTableID;
    }

    @Override
    public void setReceivers(String[] receiver) {
        this.receivers=receiver;
    }

    @Override
    public String[] getReceiver() {
        return receivers;
    }
    */
    //its exactly the same as in Chat
}
