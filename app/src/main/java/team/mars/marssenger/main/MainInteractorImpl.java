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
    
    public void sendMessage(String myNumber, String[] receivers, String message) {
        
        for(int i = 0; i<receivers.length;i++) { //TODO eh alles neu machen weil neues senden usw
                                                // TODO oh senden ist schon irgendwie neu
            new AsyncTask<String, String, JSONArray>() {
                @Override
                protected JSONArray doInBackground(String... params) {
                    String result11;
                    try {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost("HTTP://185.38.45.42:3000/messages/addMessage");

        

                        // Add your data
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                        nameValuePairs.add(new BasicNameValuePair("sender", params[0]));
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
            }.execute(myNumber, receivers[i], message);
            int intell = chatDatabase.isChatExisting(receivers[i]); // TODO ja das ist sehr behindert, muss auch neu gemacht werden
            if (intell > -1) {
                messageDatabase.createMessage(message, 1, intell, 1);
            } else {
                chatDatabase.createChat(receivers[i], receivers[i]);
                messageDatabase.createMessage(message, 1, chatDatabase.getAllChat().size() - 1, 1);
            }
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
