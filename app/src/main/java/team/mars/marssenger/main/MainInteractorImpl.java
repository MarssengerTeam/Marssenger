package team.mars.marssenger.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import team.mars.marssenger.database.ChatDatabase;
import team.mars.marssenger.database.MessageDatabase;
import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.util.Constants;

/**
 * Created by root on 03.12.14.
 */
public class MainInteractorImpl implements MainInteractor {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String TAG = "MainInteractorImpl";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    String SENDER_ID;

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    String regid;

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

        //create test chats
        createTestChats();

        //TODO GooglePlay active
        SENDER_ID = Constants.PROJECT_ID;
        gcm = GoogleCloudMessaging.getInstance(context);
        regid =getRegistrationId(context);
    }

    private void createTestChats(){
        int sender =(int) (Math.random()*2);
        if(chatDatabase.getAllChat().size()<8) {
            chatDatabase.createChat("Timo", "0157700000");
            messageDatabase.createMessage("1st Message", sender, 0, 0);

            chatDatabase.createChat("Jan Niklas", "0157712345");
            messageDatabase.createMessage("1st Message", sender, 1, 0);

            chatDatabase.createChat("Noli", "015712563485");
            messageDatabase.createMessage("last message this is really interesting oh my god!", sender, 2, 0);

            chatDatabase.createChat("Nils", "015748569521");
            messageDatabase.createMessage("this is also a really long message to see if it breaks the layout", sender, 3, 0);

            chatDatabase.createChat("Nicolas", "015784594253");
            messageDatabase.createMessage("to have a beard is to have democracy", sender, 4, 0);

            chatDatabase.createChat("Mister Null","000000000000");
            messageDatabase.createMessage("...",sender,5,0);

            chatDatabase.createChat("Miss One","111111111111");
            messageDatabase.createMessage("oneoneoneoneoneoneone",sender,6,0);

            chatDatabase.createChat("Null Jr.","015697536458");
            messageDatabase.createMessage("nullnull null null null",sender,7,0);
        }else{

            messageDatabase.createMessage("MessageTest: "+messageDatabase.getAllMessageFromChat(chatDatabase.getAllChatByTime().get(0)).size(), sender, 0, 0);
            messageDatabase.createMessage("MessageTest: "+messageDatabase.getAllMessageFromChat(chatDatabase.getAllChatByTime().get(1)).size(), sender, 1, 0);
            messageDatabase.createMessage("MessageTest: "+messageDatabase.getAllMessageFromChat(chatDatabase.getAllChatByTime().get(2)).size(), sender, 2, 0);
            messageDatabase.createMessage("MessageTest: "+messageDatabase.getAllMessageFromChat(chatDatabase.getAllChatByTime().get(3)).size(), sender, 3, 0);
            messageDatabase.createMessage("MessageTest: "+messageDatabase.getAllMessageFromChat(chatDatabase.getAllChatByTime().get(4)).size(), sender, 4, 0);
            messageDatabase.createMessage("MessageTest: "+messageDatabase.getAllMessageFromChat(chatDatabase.getAllChatByTime().get(5)).size(), sender, 5, 0);
            messageDatabase.createMessage("MessageTest: "+messageDatabase.getAllMessageFromChat(chatDatabase.getAllChatByTime().get(6)).size(), sender, 6, 0);
            messageDatabase.createMessage("MessageTest: "+messageDatabase.getAllMessageFromChat(chatDatabase.getAllChatByTime().get(7)).size(), sender, 7, 0);

        }
    }

    @Override
    public String getRegid(){return regid;}

    @Override
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (android.app.Activity) context,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    public void registerInBackground() { //TODO Complete this codesection

    }

    @Override
    public void sendRegistrationIdToBackend(){

    }

    public void registerAtServer(String phoneNumber, String email, String GCMCode, String digitCode){

    }

    @Override
    public void storeRegistrationId(Context context, String regid){

    }

    @Override
    public String getRegistrationId(Context context) {
        return null;
    }

    @Override
    public SharedPreferences getGCMPreferences(Context context) {
        return null;
    }

    public int getAppVersion(Context context) {
        return 1;
    }

    @Override
    public void sendMessage(String myNumber, String receiver, String message){

        int intell = chatDatabase.isChatExisting(receiver);
       if(intell>-1){
           messageDatabase.createMessage(message,1,intell,1);
       }else{
           chatDatabase.createChat(receiver,receiver);
           messageDatabase.createMessage(message,1,chatDatabase.getAllChat().size()-1,1);
       }

    }

    @Override
    public ArrayList<Chat> getChatsList() {
       return chatDatabase.getAllChat();
    }

    @Override
    public ChatDatabase getChatDatabase(){return chatDatabase;}

    @Override
    public MessageDatabase getMessageDataBase(){return messageDatabase;}

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

    private void test(CharSequence charSequence){
        Toast.makeText(context,charSequence,Toast.LENGTH_SHORT).show();
    }


}
