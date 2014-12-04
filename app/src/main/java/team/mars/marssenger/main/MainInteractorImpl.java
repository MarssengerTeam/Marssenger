package team.mars.marssenger.main;

import android.content.Context;

import java.util.ArrayList;

import team.mars.marssenger.database.ChatDatabase;
import team.mars.marssenger.database.MessageDatabase;
import team.mars.marssenger.datatype.Chat;

/**
 * Created by root on 03.12.14.
 */
public class MainInteractorImpl implements MainInteractor {

    //attr
    private boolean connected=false;
    private ChatDatabase chatDatabase;
    private MessageDatabase messageDatabase;
    private Context context;

    public MainInteractorImpl(Context context){
        this.context= context;

        messageDatabase = new MessageDatabase(context);
        chatDatabase = new ChatDatabase(context,messageDatabase);
        openChatDB();
        openMessageDB();

        chatDatabase.createChat("NAME", "RECEIVER");
        messageDatabase.createMessage("message","1","jetzt",0,0);


    }

    @Override
    public ArrayList<Chat> getChatsList() {
       return chatDatabase.getAllChat();
    }

    public ChatDatabase getChatDatabase(){return chatDatabase;}
    @Override
    public boolean connectionEstablished() {
        return connected;
    }

    @Override
    public void buildConnection() {
        connected=false;
    }

    @Override
    public void openChatDB() {
        chatDatabase.open();
    }

    @Override
    public void openMessageDB() {
        messageDatabase.open();
    }

    @Override
    public void closeMessageDB() {
        messageDatabase.close();
    }

    @Override
    public void closeChatDB() {
        chatDatabase.close();
    }


}
