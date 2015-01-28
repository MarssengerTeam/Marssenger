package team.mars.marssenger.main;

import android.app.Application;

import team.mars.marssenger.custom.CChatListAdapter;
import team.mars.marssenger.database.DatabaseWrapper;
import team.mars.marssenger.database.MessageDatabase;
import team.mars.marssenger.datatype.Chat;

/**
 * Created by Jan-Niklas on 26.01.2015.
 */
public class Marssenger extends Application {
    private static Application instance;
    private DatabaseWrapper database;
    private boolean userActive=false;
    private Chat activeChat;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    public static Application getInstance(){
        return instance;
    }
    public synchronized DatabaseWrapper getDatabase(){
        if(database==null){
            database = new DatabaseWrapper(getInstance());
        }
        return database;
    }
    public synchronized boolean isUserActive(){
        return userActive;
    }
    public void setUserActive(boolean active){
        userActive= active;
    }
    public synchronized Chat getActiveChat(){
        return activeChat;
    }
    public void setActiveChat(Chat chat){
        activeChat=chat;
    }
}
