package team.mars.marssenger.main;

import java.util.ArrayList;

/**
 * Created by root on 03.12.14.
 */
public class MainInteractorImpl implements MainInteractor {

    //attr
    private boolean connected=false;

    public MainInteractorImpl(){
        //build connection to server?
        //open DB?
    }

    @Override
    public ArrayList<String> getChatsList() {
        //get List of all chats - not messages
        return null;
    }

    @Override
    public boolean connectionEstablished() {
        return connected;
    }

    @Override
    public void buildConnection() {
        connected=false;
    }

    @Override
    public void openDB() {

    }
}
