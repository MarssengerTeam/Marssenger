package team.mars.marssenger.communication;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
/**
 * Created by root on 03.12.14.
 */
public class ComSending {
    /*Explanation
        0. Param: URL
        1. POST = 0, GET = 1
        2. REQTYPE 3. DATA
        n. REQTYPE n+1. DATA
     */


    //Log
    public static final String TAG = "ComSending";

    //Attributes
    private String URL;
    //end Attributes


    //Constructor
    public ComSending(String URL){
        this.URL=URL;
    }

    public AsyncTask sendTextMessage(String receiver, String message){
        return new SendingTask().execute(this.URL, "0", receiver, message);
    }

    public AsyncTask getNewMessages(){
        return new SendingTask().execute(this.URL, "1");
    }

    private class SendingTask extends AsyncTask<String, Integer, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... definitions) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                                                    //URL
                HttpPost httppost = new HttpPost(definitions[0]);

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(definitions.length-2);
                for(int i=1;i<definitions.length;i=i+2){
                    nameValuePairs.add(new BasicNameValuePair(definitions[i], definitions[i+1]));
                    Log.d(TAG, definitions[i]+", "+definitions[i+1]);
                }
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line = "0";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                String result11 = sb.toString();

                // parsing data
                return new JSONArray(result11);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(JSONArray result) {

        }
    }
}
