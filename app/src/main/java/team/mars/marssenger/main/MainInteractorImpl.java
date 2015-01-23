package team.mars.marssenger.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
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

import team.mars.marssenger.communication.HttpsBackgroundService;
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
    private static final String PROPERTY_MY_PHONENUMBER = "myPhoneNumber";
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

    //HttpsService
    private HttpsBackgroundService mService;
    private boolean isBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            HttpsBackgroundService.myBinder binder = (HttpsBackgroundService.myBinder) service;
            mService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    /*
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, HttpsBackgroundService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBound){
            unbindService(mConnection);
            isBound=false;
        }
    }*/

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
        regid = null;
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

            chatDatabase.createChat("1st Group","0157700000;0157712345;0157700003;0157700010;");
            messageDatabase.createMessage("GroupMessage to 0157700000;0157712345;0157700003;0157700010;", sender, 8, 0);
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

    public void setMyPhoneNumber(String phoneNumber){
        final SharedPreferences prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_MY_PHONENUMBER, phoneNumber);
        editor.commit();
    }

    public String getMyPhoneNumber(){
        final SharedPreferences prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        return prefs.getString(PROPERTY_MY_PHONENUMBER, "null");
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
