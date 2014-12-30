package team.mars.marssenger.communication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.LocalServerSocket;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
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

import team.mars.marssenger.main.MainActivity;
import team.mars.marssenger.main.MainInteractor;
import team.mars.marssenger.main.MainInteractorImpl;

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
    int StartMode = START_STICKY;
    IBinder mBinder;
    boolean mAllowRebind = true;
    //
    //For Server Connection
    private GoogleCloudMessaging gcm;
    private HttpClient httpClient;
    private Handler myHandler;

    //Important Strings
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    //to Indentify
    private String senderID;
    private String myRegID="";
    private String myPhoneNumber;
    private String myEmail;

    //Controlling
    private boolean isVerified=false;



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

        if(!isVerified){
            //first run
            if(myRegID.isEmpty()){
                //when regId not initialized
                String unverifiedId = getRegistrationId(getBaseContext());
                if(unverifiedId.isEmpty()){
                    //either appVersion old or no Id stored
                    registerInBackground();
                }else{
                    //id found but needs to check
                    //TODO checkRegistrationId(unverifiedId);
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
                    isVerified=true;
                }
                myHandler.postDelayed(this, 10000);
            }
        }, 0);
        return StartMode;
    }

    public void checkRegistrationId(String regID) {
        new AsyncTask<String, String, JSONArray>() {
            @Override
            protected JSONArray doInBackground(String... params) {
                String result11;
                try {
                    //TODO new function to Verify regID
                    HttpPost httppost = new HttpPost("HTTP://185.38.45.42:3000/user/register");

                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("phoneNumber", params[0]));

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
                Log.i("BackgroundService", "Hi");
            }
        }.execute(regID);
    }


    private void controlConnection(){
        //Toast.makeText(getBaseContext(), "BackgroundService: Controlling", Toast.LENGTH_SHORT).show();
        //TODO controlling Connection



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

    public void sendMessage(String myNumber, String receiver, String message){
        new AsyncTask<String, String, JSONArray>() {
            @Override
            protected JSONArray doInBackground(String... params) {
                String result11;
                try {
                    //Now using existing client
                    HttpPost httppost = new HttpPost("HTTP://185.38.45.42:3000/messages/addMessage");


                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                    nameValuePairs.add(new BasicNameValuePair("sender",params[0]));
                    nameValuePairs.add(new BasicNameValuePair("receiver", params[1]));
                    nameValuePairs.add(new BasicNameValuePair("data", params[2]));
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
        }.execute(myNumber,receiver,message);
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
                        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
                    }
                    Log.d("BackgroundService", "trying to register at gcm");
                    myRegID = gcm.register(senderID);
                    msg = "Device registered, registration ID=" + myRegID;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.

                    registerAtServer(myPhoneNumber, myEmail, myRegID, "1234Lol");


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


    public void sendRegistrationIdToBackend(){
        if(gcm !=null) {
            gcm = GoogleCloudMessaging.getInstance(getBaseContext());
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

                    Log.d("BackgroundService", "Sending regId to server");
                    gcm.send(senderID + "@gcm.googleapis.com", "0", data);
                    msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                Log.i("BackgroundService", s);
            }
        }.execute(myRegID);
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
                Log.i("BackgroundService", "Hi");
            }
        }.execute(phoneNumber, GCMCode,digitCode, email);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class myBinder extends Binder{
        public HttpsBackgroundService getService(){
            return HttpsBackgroundService.this;
        }
    }
}
