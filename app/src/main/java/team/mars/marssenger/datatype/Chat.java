package team.mars.marssenger.datatype;



public interface Chat {

    public Boolean isSingleChat();

    public long getId();
    public void setId(long id);


    public String getName();
    public void setName(String name);

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString();


    public void setMessageTableID(long id);
    public long getMessageTableID();



    public void setReceivers(String[] reciever);
    public String[] getReceiver();
}