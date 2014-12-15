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
        int sender =(int) Math.random()*2;
        if(chatDatabase.getAllChat().size()>3){
            messageDatabase.createMessage("Alarm Alarm!", sender, 0, 0);
            messageDatabase.createMessage("hier steht eine Nachricht",sender,1,0);
            messageDatabase.createMessage("jhdhjuswzjdh",sender,2,0);
            messageDatabase.createMessage("azerty", sender,3,0);
        }else{
            chatDatabase.createChat("DER chat", "<handynummer>");
            messageDatabase.createMessage("Alarm Alarm!", sender, 0, 0);
            chatDatabase.createChat("Noch ein chat", "<handynummer>");
            messageDatabase.createMessage("hier steht eine Nachricht",sender,1,0);
            chatDatabase.createChat("adsvkdjhj", "<handynummer>");
            messageDatabase.createMessage("jhdhjuswzjdh",sender,2,0);
            chatDatabase.createChat("qwerty", "<handynummer>");
            messageDatabase.createMessage("azerty", sender,3,0);
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

    public void registerAtServer(String phoneNumber, String email, String GCMCode, String digitCode){
        new AsyncTask<String, String, JSONArray>() {
            @Override
            protected JSONArray doInBackground(String... params) {
                String result11;
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("HTTP://185.38.45.42:3000/user/register");

                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                    nameValuePairs.add(new BasicNameValuePair("phoneNumber", params[0]));
                    nameValuePairs.add(new BasicNameValuePair("GCMCode", params[1]));
                    nameValuePairs.add(new BasicNameValuePair("digitCode", params[2]));
                    nameValuePairs.add(new BasicNameValuePair("eMail", params[3]));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    Log.d("SendingService", "Vor senden");
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d("SendingService", "Nach senden");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    sb.append(reader.readLine() + "\n");
                    String line = "0";
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    reader.close();
                    Log.d("Sending", "Sendet daten!");
                    result11 = sb.toString();
                    return new JSONArray(result11);
                    // parsing data
                } catch (Exception e) {
                    Log.d("SendingService", e.toString());
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONArray result) {
                Log.i(TAG, "Hi");
            }
        }.execute(phoneNumber, GCMCode,digitCode, email);
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
    public void sendMessage(String myNumber, String receiver, String message){
        new AsyncTask<String, String, JSONArray>() {
            @Override
            protected JSONArray doInBackground(String... params) {
                String result11;
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("HTTP://185.38.45.42:3000/messages/addMessage");

                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                    nameValuePairs.add(new BasicNameValuePair("sender",params[0]));
                    nameValuePairs.add(new BasicNameValuePair("receiver", params[1]));
                    nameValuePairs.add(new BasicNameValuePair("data", params[2]));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    Log.d("SendingService", "Vor senden");
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d("SendingService", "Nach senden");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    sb.append(reader.readLine() + "\n");
                    String line = "0";
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    reader.close();
                    Log.d("Sending", "Sendet daten!");
                    result11 = sb.toString();
                    return new JSONArray(result11);
                    // parsing data
                } catch (Exception e) {
                    Log.d("SendingService", e.toString());
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONArray result) {
                Log.i(TAG, "Hi");
            }
        }.execute(myNumber, receiver,message);

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
