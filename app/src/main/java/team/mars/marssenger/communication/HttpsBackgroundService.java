package team.mars.marssenger.communication;

import android.app.Service;
import android.content.Intent;
import android.net.LocalServerSocket;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
    private HttpClient httpClient;
    private Handler myHandler;


    //to Indentify
    private String myPhoneNumber;
    private String myRegID;

    private int counter=0;


    @Override
    public void onCreate() {
        //Gets Called when Service starts

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //myPhoneNumber = intent.getStringExtra("phoneNumber");
        //myRegID = intent.getStringExtra("regID");

        httpClient = new DefaultHttpClient();
        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                controlConnection();
                myHandler.postDelayed(this, 15000);
            }
        }, 15000);
        return StartMode;
    }

    private void controlConnection(){
        Toast.makeText(getBaseContext(), "BackgroundService: Controlling", Toast.LENGTH_SHORT).show();

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
