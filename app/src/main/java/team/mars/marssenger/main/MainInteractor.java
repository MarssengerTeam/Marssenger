package team.mars.marssenger.main;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import team.mars.marssenger.database.ChatDatabase;
import team.mars.marssenger.datatype.Chat;

/**
 * Created by root on 03.12.14.
 */
public interface MainInteractor {

    //Methoden für den Zugriff auf die Daten
    public ArrayList<Chat> getChatsList();

    public boolean connectionEstablished();
    public ChatDatabase getChatDatabase();
    public void buildConnection();

    public void openChatDB();

    public void openMessageDB();
    public void closeMessageDB();
    public void closeChatDB();

    public boolean checkPlayServices();
    public void registerInBackground();
    public String getRegistrationId(Context context);
    public SharedPreferences getGCMPreferences(Context context);
    public int getAppVersion(Context context);
    public void sendMessage(String message);
    public String getRegid();
    public void sendRegistrationIdToBackend();
    public void storeRegistrationId(Context context, String regid);

}
