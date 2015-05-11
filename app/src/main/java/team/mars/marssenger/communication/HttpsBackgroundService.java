package team.mars.marssenger.communication;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import team.mars.marssenger.database.DatabaseWrapper;
import team.mars.marssenger.main.MainActivity;
import team.mars.marssenger.main.Marssenger;

/**
 * Created by Jan-Niklas on 11.05.2015
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
    private final IBinder mBinder = new myBinder();
    boolean mAllowRebind = true;

    //CONSTANTS
    private static final String GCM_CONNECTION      = "GCM_CONNECTION";
    private static final String SERVER_POST         = "SERVER_POST";
    private static final String SERVER_REGISTER     = "SERVER_REGISTER";
    private static final String SERVER_SEND_MESSAGE = "SERVER_SEND_MESSAGE";
    private static final String SERVER_GET_MESSAGES = "SERVER_GET_MESSAGES";
    private static final String SERVER_CHECK_REG_ID = "SERVER_CHECK_REG_ID";
    private static final String SERVER_CREATE_GROUP = "SERVER_CREATE_GROUP";

    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 627777777;

    //Important Strings
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    //LOG
    private static final String LOG="BACKGROUND_SERVICE";
    private boolean isUserActive;

    //Controlling
    private Handler myHandler;
    private boolean isVerified = false;
    private boolean isBound = false;

    private DatabaseWrapper database;

    //to Indentify
    private String senderID;
    private String myRegID = "";
    private String myPhoneNumber;
    private String myEmail;
    private String myDigitCode;
    //


    GoogleCloudMessaging gcm = null;
    Networking mNetwork = new Networking();



    @Override
    public void onCreate() {
        super.onCreate();
        super.onCreate();
        Marssenger mars = ((Marssenger)Marssenger.getInstance());
        database=mars.getDatabase();
        isUserActive=mars.isUserActive();
        gcm = GoogleCloudMessaging.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        senderID = intent.getStringExtra("senderID");
        myPhoneNumber = intent.getStringExtra("phoneNumber");
        myEmail = intent.getStringExtra("email");
        myDigitCode = intent.getStringExtra("digitCode");

        if (!isVerified) {
            //first run
            Log.d(LOG, "Not Verified!");
            if (myRegID.isEmpty()) {
                //when regId not initialized
                String unverifiedId = getRegistrationId(getBaseContext());
                Log.d("ConnectionHandler", "got reg id");
                if (unverifiedId.isEmpty()) {
                    //either appVersion old or no Id stored
                    Log.d("ConnectionHandler", "either appVersion old or no Id stored");
                    registerOnGCM();
                } else {
                    //id found but needs to check
                    Log.d("ConnectionHandler", "id found but needs to check");
                    checkRegistrationId(unverifiedId);
                    Toast.makeText(getBaseContext(), "Has Id", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Log.d(LOG, "Already Verified!");
        }

        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isVerified) {
                    //controlConnection();
                } else {
                    //TODO check weather connection verified
                    //Toast.makeText(getBaseContext(), "Verifed", Toast.LENGTH_LONG).show();
                    //Log.d("ConnectionHandler", "Not verified");
                }
                myHandler.postDelayed(this, 1000);
            }
        }, 0);
        return StartMode;
    }


    public void registerOnGCM(){
        new Networking().execute(GCM_CONNECTION);
    }

    public void registerOnServer(String phoneNumber, String email, String GCMCode, String digitCode){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("phoneNumber", phoneNumber));
        nameValuePairs.add(new BasicNameValuePair("GCMCode", GCMCode));
        nameValuePairs.add(new BasicNameValuePair("digitCode", digitCode));
        nameValuePairs.add(new BasicNameValuePair("eMail", email));

        new Networking().execute(SERVER_POST, SERVER_REGISTER, nameValuePairs);
    }

    private void checkRegistrationId(String regID) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("regID", regID));
        nameValuePairs.add(new BasicNameValuePair("phoneNumber", myPhoneNumber));

        new Networking().execute(SERVER_POST, SERVER_CHECK_REG_ID, nameValuePairs);
    }


    public void sendMessage(String receiver, String message, String messageID){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("sender", myPhoneNumber));
        nameValuePairs.add(new BasicNameValuePair("receiver", receiver));
        nameValuePairs.add(new BasicNameValuePair("data", message));
        nameValuePairs.add(new BasicNameValuePair("messageID", messageID));

        new Networking().execute(SERVER_POST, SERVER_SEND_MESSAGE, nameValuePairs);
    }

    public void createGroup(final String groupName, String[] member){
        JSONObject jsonObject = new JSONObject();
        for(int i = 0; i<member.length;i++){
            try {
                jsonObject.put("member", member[i]);
            } catch (JSONException e) {

                Log.e("JSONParsing", e.toString());
            }
        }

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("groupName", groupName));
        nameValuePairs.add(new BasicNameValuePair("member", jsonObject.toString()));

        mNetwork.execute(SERVER_POST, SERVER_CREATE_GROUP, nameValuePairs);
    }

    public void getMessages(){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("number", myPhoneNumber));

        mNetwork.execute(SERVER_POST, SERVER_GET_MESSAGES, nameValuePairs);
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


    private class Networking extends AsyncTask<Object, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(Object[] params) {
            JSONObject result = new JSONObject();
            //Generic
            switch ((String)params[0]){
                case GCM_CONNECTION:
                    result=GCMregister();
                    break;
                case SERVER_POST:
                    result=serverPost(params);
                    break;

                default:
                    //Something went wrong
                    break;

            }
            return result;
        }

        private JSONObject GCMregister(){
            JSONObject msg = new JSONObject();
            try {
                msg.put("case", GCM_CONNECTION);
                myRegID = gcm.register(senderID);
                msg.put("msg", "Device registered, registration ID=" + myRegID);

                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.
                registerOnServer(myPhoneNumber, myEmail, myRegID, myDigitCode);

                // For this demo: we don't need to send it because the device
                // will send upstream messages to a server that echo back the
                // message using the 'from' address in the message.

                // Persist the registration ID - no need to register again.
                storeRegistrationId(getBaseContext(), myRegID);
            } catch (IOException ex) {
                try {
                    msg.put("msg","Error :" + ex.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return msg;
        }
        public void storeRegistrationId(Context context, String regid) {
            final SharedPreferences prefs = getGCMPreferences(context);
            int appVersion = getAppVersion(context);
            Log.i("BackgroundService", "Saving regId on app version " + appVersion);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PROPERTY_REG_ID, regid);
            editor.putInt(PROPERTY_APP_VERSION, appVersion);
            editor.commit();
        }




        private void sendRegistrationIdToBackend() {
        }

        private JSONObject serverPost(final Object[] params){
            String resultString;
            try{
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = null;
                String currentCase = "";
                switch((String)params[1]){
                    case SERVER_REGISTER:
                        httpPost = new HttpPost("HTTP://185.38.45.42:3000/user/register");
                        currentCase = SERVER_REGISTER;
                        break;
                    case SERVER_SEND_MESSAGE:
                        httpPost = new HttpPost("HTTP://185.38.45.42:3000/messages/addMessage");
                        currentCase = SERVER_SEND_MESSAGE;
                        break;
                    case SERVER_CHECK_REG_ID:
                        httpPost = new HttpPost("HTTP://185.38.45.42:3000/user/getAuthTokenByPhonenumberAndGCMCode");
                        currentCase = SERVER_CHECK_REG_ID;
                        break;
                    case SERVER_CREATE_GROUP:
                        httpPost = new HttpPost("HTTP://185.38.45.42:3000/groups/createGroup");
                        currentCase = SERVER_CREATE_GROUP;
                        break;
                    case SERVER_GET_MESSAGES:
                        httpPost = new HttpPost("HTTP://185.38.45.42:3000/messages/getMessages");
                        currentCase = SERVER_GET_MESSAGES;
                        break;
                    default:
                        currentCase = "error";
                        break;
                }

                ArrayList<NameValuePair> items = (ArrayList<NameValuePair>) params[2];

                httpPost.setEntity(new UrlEncodedFormEntity(items));

                HttpResponse response = httpClient.execute(httpPost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line = "0";
                int counter=0;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                    counter++;
                }
                Log.d(LOG, String.valueOf(counter));
                reader.close();
                resultString = sb.toString();
                Log.d(LOG, resultString);

                JSONObject resultObject;
                if(resultString.startsWith("{")){
                    resultObject = new JSONObject(resultString);
                }else{
                    JSONArray resultArray = new JSONArray(resultString);
                    resultObject = resultArray.getJSONObject(0);
                }

                resultObject.put("case", currentCase);
                return resultObject;

            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                String currentCase = result.getString("case");
                switch (currentCase){
                    case GCM_CONNECTION:
                        Log.d(LOG, result.getString("msg"));
                        break;
                    case SERVER_REGISTER:
                        if(result.getString("error").isEmpty()){
                            Log.d(LOG, "Register worked fine!");
                        }else{
                            Log.d(LOG, result.getString("error"));
                            //TODO tell user what went wrong
                        }
                        break;
                    case SERVER_CHECK_REG_ID:
                        //TODO Noli mach mal XD
                        break;
                    case SERVER_SEND_MESSAGE:
                        Log.d(LOG, result.toString());
                        break;
                    case SERVER_GET_MESSAGES:
                        Log.d(LOG, result.toString());
                        break;
                    case SERVER_CREATE_GROUP:
                        Log.d(LOG, result.toString());
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Returns Interface for Clients
        isBound = true;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;
        return mAllowRebind;
    }

    public class myBinder extends Binder {
        public HttpsBackgroundService getService() {
            return HttpsBackgroundService.this;
        }
    }
}
