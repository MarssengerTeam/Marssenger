package team.mars.marssenger.main;

import java.util.ArrayList;

import team.mars.marssenger.database.ChatDatabase;
import team.mars.marssenger.datatype.Chat;

/**
 * Created by root on 03.12.14.
 */
public interface MainInteractor {

    //Methoden für den Zugriff auf die Daten
    public ArrayList<Chat> getChatsList();
    public ChatDatabase getChatDatabase();
    public boolean connectionEstablished();

    public void buildConnection();

    public void openChatDB();

    public void openMessageDB();
    public void closeMessageDB();
    public void closeChatDB();
}
