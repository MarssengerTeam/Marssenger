package team.mars.marssenger.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
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

    TextView mDisplay;
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
        chatDatabase.createChat("DER chat", "Der Empfänger");
        messageDatabase.createMessage("Alarm Alarm!", 1, 0, 0);
        chatDatabase.createChat("Noch ein chat", "empf");
        messageDatabase.createMessage("hier steht eine Nachricht",1,1,0);
        chatDatabase.createChat("adsvkdjhj", "lfindjh");
        messageDatabase.createMessage("jhdhjuswzjdh",1,2,0);
        chatDatabase.createChat("qwerty", "qewertz");
        messageDatabase.createMessage("azerty", 1,3,0);
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
        AsyncTask at = new AsyncTask(){

            @Override
            protected void onPostExecute(Object msg) {
                String message=String.valueOf(msg);
               //TODO mDisplay.append(message + "\n");
            }

            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    Log.d(TAG, "trying to register at gcm");
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }
        };
        at.execute();
    }

    @Override
    public void sendRegistrationIdToBackend(){
        if(gcm !=null) {
            gcm = GoogleCloudMessaging.getInstance(context);
        }
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                try {
                    Bundle data = new Bundle();
                    //Information for the Server
                    data.putString("", params[0]);
                    data.putString("my_action","register");

                    Log.d(TAG, "Sending regId to server");
                    gcm.send(SENDER_ID + "@gcm.googleapis.com", "0", data);
                    msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                Log.i(TAG, s);
            }
        }.execute(regid);
    }

    public void registerAtServer(String phoneNumber, String email){
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                try {
                    Log.d(TAG, "Trying to write a message to Server");
                    Bundle data = new Bundle();
                    data.putString("phoneNumber", params[0]);
                    data.putString("my_action", params[1]);
                    gcm.send(SENDER_ID + "@gcm.googleapis.com", regid, data);
                    msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
            }
        }.execute(phoneNumber, email);
    }

    @Override
    public void storeRegistrationId(Context context, String regid){
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regid);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    @Override
    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    @Override
    public SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    public int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    public void sendMessage(String receiver, String message){
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                try {
                    Log.d(TAG, "Trying to write a message to Server");
                    Bundle data = new Bundle();
                    data.putString("sender",regid);
                    data.putString("receiver", params[0]);
                    data.putString("data", params[1]);
                    gcm.send(SENDER_ID + "@gcm.googleapis.com", regid, data);
                    msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
            }
        }.execute(receiver,message);

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
