package team.mars.marssenger.datatype;


public class Chat {
    private long id;
    private String name;
    private String number;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return name;
    }
}