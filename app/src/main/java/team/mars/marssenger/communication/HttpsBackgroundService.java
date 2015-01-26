package team.mars.marssenger.communication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import team.mars.marssenger.chat.ChatActivity;
import team.mars.marssenger.database.ChatDatabase;
import team.mars.marssenger.R;
import team.mars.marssenger.database.MessageDatabase;
import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.main.MainActivity;
import team.mars.marssenger.main.MainInteractor;
import team.mars.marssenger.main.MainInteractorImpl;
import team.mars.marssenger.main.Marssenger;


/**
 * Created by Noli on 25.12.2014.
 *
 * What is does:
 *  1. When app started, Service gets started
 *  2. Service registeres at GCM
 *  3. asks server whether gcm is correct an whether you can start chatting
 *  4. service gets bind when you want to perform a message
 *  5. accepts messages from server and pulls messages
 */
public class HttpsBackgroundService extends Service {
    //Service specific
    int StartMode = START_REDELIVER_INTENT;
    private final IBinder mBinder  = new myBinder();
    boolean mAllowRebind = true;
    //

    private MainInteractorImpl mainInteractor;

    //For Server Connection
    private GoogleCloudMessaging gcm;
    private HttpClient httpClient;
    private Handler myHandler;
    //
    //Important Strings
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    //
    //to Indentify
    private String senderID;
    private String myRegID="";
    private String myPhoneNumber;
    private String myEmail;
    private String myDigitCode;
    //
    //Controlling
    private boolean isVerified=false;
    //

    //Notification
    private NotificationManager mNotificationManager;
    private int NOTIFICATION_ID = 627777777;
    private ArrayList<String> notItems = new ArrayList<String>();

    //Service Methods
    @Override
    public void onCreate() {
        //Gets Called when Service starts
        super.onCreate();

        httpClient = new DefaultHttpClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        senderID = intent.getStringExtra("senderID");
        myPhoneNumber = intent.getStringExtra("phoneNumber");
        myEmail = intent.getStringExtra("email");
        myDigitCode = intent.getStringExtra("digitCode");

        if(!isVerified){
            //first run
            if(myRegID.isEmpty()){
                //when regId not initialized
                String unverifiedId = getRegistrationId(getBaseContext());
                Log.d("ConnectionHandler", "got reg id");
                if(unverifiedId.isEmpty()){
                    //either appVersion old or no Id stored
                    Log.d("ConnectionHandler", "either appVersion old or no Id stored");
                    registerInBackground();
                }else{
                    //id found but needs to check
                    Log.d("ConnectionHandler", "id found but needs to check");
                    checkRegistrationId(unverifiedId);
                    Toast.makeText(getBaseContext(), "Has Id", Toast.LENGTH_SHORT).show();
                }
            }
        }

        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isVerified) {
                    controlConnection();
                }else{
                    //TODO check weather connection verified
                    //Toast.makeText(getBaseContext(), "Verifed", Toast.LENGTH_LONG).show();
                    Log.d("ConnectionHandler", "Not verified");
                }
                myHandler.postDelayed(this, 1000);
            }
        }, 0);
        return StartMode;
    }

    private void controlConnection(){
        //Toast.makeText(getBaseContext(), "BackgroundService: Controlling", Toast.LENGTH_SHORT).show();
        //TODO controlling Connection

        //Log.d("ConnectionHandler", "Controlling");
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Returns Interface for Clients
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class myBinder extends Binder{
        public HttpsBackgroundService getService(){
            return HttpsBackgroundService.this;
        }
    }
    //END Service Methods

    //Communication functions
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
                        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
                    }
                    Log.d("BackgroundService", "trying to register at gcm");
                    myRegID = gcm.register(senderID);
                    msg = "Device registered, registration ID=" + myRegID;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.

                    registerAtServer(myPhoneNumber, myEmail, myRegID, myDigitCode);


                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(getBaseContext(), myRegID);
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

    public void registerAtServer(String phoneNumber, String email, String GCMCode, String digitCode){
        new AsyncTask<String, String, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
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

                    HttpEntity entity = response.getEntity();
                    if(entity != null){
                        InputStream inputStream = entity.getContent();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder sb = new StringBuilder();

                        String line = null;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        String resultString = sb.toString().substring(1, sb.length()-1);

                        JSONObject jsonObject = new JSONObject(resultString);
                        Log.i("SendingService", jsonObject.toString());

                        return jsonObject;
                    }
                    return null;
                    // parsing data
                } catch (Exception e) {
                    Log.d("SendingService", e.toString());
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                try {
                    if(result.toString()!="") {
                        Log.i("SendingService", result.toString());
                    }
                    if(result.getString("error")=="1"){
                        Toast.makeText(getBaseContext(), "Phonenumber already in use", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(phoneNumber, GCMCode,digitCode, email);
    }

    public void storeRegistrationId(Context context, String regid){
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i("BackgroundService", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regid);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    public void checkRegistrationId(final String regID) {
        new AsyncTask<String, String, JSONArray>() {
            @Override
            protected JSONArray doInBackground(String... params) {
                String result11="123";
                try {

                    HttpPost httppost = new HttpPost("HTTP://185.38.45.42:3000/user/isVerified");

                    Log.d("SendingService", params[1]+" : "+params[0]);

                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("regID", params[0]));
                    nameValuePairs.add(new BasicNameValuePair("phoneNumber", params[1]));

                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    // Execute HTTP Post Request
                    HttpResponse response = httpClient.execute(httppost);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    sb.append(reader.readLine() + "\n");
                    String line = "0";
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    reader.close();
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
                //TODO replace with real code
                try{
                    /*if(result.getJSONObject(0).getString("regID") == regID){
                        setVerified(true);
                    }*/
                    Log.d("What", result.toString());
                    isVerified = true;
                }catch (Exception e){
                    e.toString();
                }

            }
        }.execute(regID, myPhoneNumber);
    }

    private void setVerified(boolean b){
        isVerified = b;
    }

    public void sendMessage(String receiver, String message, String messageID){
        Log.d("SendingService", message);

        new AsyncTask<String, String, JSONArray>() {
            @Override
            protected JSONArray doInBackground(String... params) {
                String result11;
                try {
                    //Now using existing client
                    HttpPost httppost = new HttpPost("HTTP://185.38.45.42:3000/messages/addMessage");


                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                    nameValuePairs.add(new BasicNameValuePair("sender",params[0]));
                    nameValuePairs.add(new BasicNameValuePair("receiver", params[1]));
                    nameValuePairs.add(new BasicNameValuePair("data", params[2]));
                    nameValuePairs.add(new BasicNameValuePair("messageID",params[3]));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    Log.d("SendingService", "Vor senden");
                    HttpResponse response = httpClient.execute(httppost);
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

            }
        }.execute(myPhoneNumber,receiver,message, messageID);
    }

    public void getMessages(){
        new AsyncTask<String, String, JSONArray>() {
            @Override
            protected JSONArray doInBackground(String... params) {
                String result11;
                try {
                    //Now using existing client
                    HttpPost httppost = new HttpPost("HTTP://185.38.45.42:3000/messages/getMessages");


                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                    nameValuePairs.add(new BasicNameValuePair("number",params[0]));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpClient.execute(httppost);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    sb.append(reader.readLine() + "\n");
                    String line = "0";
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    reader.close();
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
                Log.d("SendingService", result.toString());
                sendNotification(result);
                addToDB(result);
            }
        }.execute(myPhoneNumber);
    }

    public void addToDB(JSONArray result){
        Log.d("DB", "Addtodb");
        try {
            Marssenger marssenger = (Marssenger) getApplicationContext();
            MessageDatabase mDataBase = marssenger.mainInteractor.getMessageDataBase();
            ChatDatabase cDataBase = marssenger.mainInteractor.getChatDatabase();

            Chat senderChat = null;
            ArrayList<Chat> chats = cDataBase.getAllChat();
            for(int i=0;i<result.length();i++) {

                for (Chat c : chats) {

                    if (result.getJSONObject(i).getString("sender").equals(c.getReceiver())) {
                        Log.e("addToDB",result.getJSONObject(i).getString("sender")+"=="+c.getReceiver());
                        senderChat = c;
                    }
                }
                if(senderChat==null){
                    Log.e("addToDB","NEW CHAT FOR NUMMBER "+result.getJSONObject(i).getString("sender"));
                    cDataBase.createChat(result.getJSONObject(i).getString("sender"),result.getJSONObject(i).getString("sender"),0);
                    senderChat = cDataBase.getAllChat().get(cDataBase.getAllChat().size()-1);
                }
                mDataBase.createMessage(result.getJSONObject(i).getString("data"), 0,senderChat.getId()-1, 0, 0);//TODO TYPE
                ChatActivity.cChatListAdapter.addMessage(cDataBase.getLastMessage(senderChat));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendNotification(JSONArray result) {
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.putExtra("StartMode", "Notification");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notIntent, 0);

        convertJsonIntoArrayList(result);

        mNotificationManager =(NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher);//TODO ic_stat_gcm

        if(notItems.size()>1) {
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle("Marssenger: " +notItems.size()+ " Messages");
            mBuilder.setContentTitle("Marssenger: " + notItems.size() + " Messages");
            mBuilder.setContentText(notItems.get(0));
            for (int i = 0; i < notItems.size(); i++) {
                inboxStyle.addLine(notItems.get(i));
            }
            mBuilder.setStyle(inboxStyle);
        }else{
            mBuilder.setContentTitle("Marssenger");
            mBuilder.setContentText(notItems.get(0));
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.addAction(R.drawable.ic_action_read, "Read", resultPendingIntent);
        mBuilder.addAction(R.drawable.ic_launcher, "Fast Re", resultPendingIntent);
        mBuilder.setContentIntent(resultPendingIntent);

        mBuilder.setLights(Color.GREEN, 100, 500);
        long[] pattern = {500, 100, 100};
        mBuilder.setVibrate(pattern);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    private void convertJsonIntoArrayList(JSONArray items){
        String result = "";
        try{
            for(int i = 0; i<items.length(); i++){
                notItems.add(items.getJSONObject(i).getString("sender")+": "+ items.getJSONObject(i).getString("data"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearNotification(){
        this.notItems.clear();
    }

    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("BackgroundService", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("BackgroundService", "App version changed.");
            return "";
        }
        return registrationId;
    }

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
    //END Communication functions
}
