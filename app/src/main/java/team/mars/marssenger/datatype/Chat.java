package team.mars.marssenger.datatype;


public class Chat {
    private long id;
    private String name;
    private long messageTableID;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessageTableID(long id){
      this.messageTableID=id;
    }

    public long getMessageTableID() {
        return messageTableID;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return name;
    }
}