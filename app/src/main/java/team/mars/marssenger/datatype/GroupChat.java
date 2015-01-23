package team.mars.marssenger.datatype;

import java.util.ArrayList;

/**
 * Created by Kern on 28.12.2014.
 */
public class GroupChat implements Chat {
    private String[] receivers;
    private String name;
    private long id;
    private long messageTableID;
    final Boolean type = false;


    @Override
    public Boolean isSingleChat() {
        return type;
    }

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
    public void setMessageTableID(long id) {
        this.messageTableID=id;
    }

    @Override
    public long getMessageTableID() {
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
}
