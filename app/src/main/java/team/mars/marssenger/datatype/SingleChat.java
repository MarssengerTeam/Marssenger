package team.mars.marssenger.datatype;

/**
 * Created by Kern on 28.12.2014.
 */

public class SingleChat implements Chat {

    private String[] recivers;
    private String name;
    private long id;
    private long messageTableID;
    final Boolean type = true;



    @Override
    public Boolean isSingleChat() {
        return null;
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
    public void setReceivers(String[] reciever) {
        this.recivers=reciever;
    }

    @Override
    public String[] getReciever() {
        return recivers;
    }
}
