package team.mars.marssenger.main;

import android.app.Application;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import team.mars.marssenger.communication.HttpsBackgroundService;
import team.mars.marssenger.database.ChatDatabase;
import team.mars.marssenger.database.DatabaseWrapper;
import team.mars.marssenger.database.MessageDatabase;
import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.util.Constants;

/**
 * Created by root on 03.12.14.
 */
public class MainInteractorImpl implements MainInteractor {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String TAG = "MainInteractorImpl";
    //SharedPreferences
    public static final String PREF_NAME = "Account";
    public static final String PROPERTY_PHONE_NUMBER = "myPhoneNumber";
    public static final String PROPERTY_E_MAIL = "myEMail";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private static final int NOTIFICATION_ID = 627777777;
    public static final File pictureFolder = new File(Environment.getExternalStorageDirectory()+ "/Marssenger/Pictures");

    String SENDER_ID;

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    String regid;

    //attr
    private boolean connected=false;
    private Context context;

    //HttpsService
    private HttpsBackgroundService mService;
    private boolean isBound = false;

    private DatabaseWrapper database;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            HttpsBackgroundService.myBinder binder = (HttpsBackgroundService.myBinder) service;
            mService = binder.getService();
            isBound = true;
            cancelNotification();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };


    public MainInteractorImpl(Context context){
        this.context = context;
        Marssenger mars = ((Marssenger)Marssenger.getInstance());
        database=mars.getDatabase();



        boolean success;
        if (!pictureFolder.exists()) {
            success = pictureFolder.mkdirs();
            if (success) {
                Log.i(MainInteractor.class.toString(),"Could Create 'Picture' Folder");
            } else {
                Log.e(MainInteractor.class.toString(),"Could not Create 'Picture' Folder");
            }
        }

        /*Intent intent = new Intent(context, HttpsBackgroundService.class);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);*/

        //create test chats
        createTestChats();

        //TODO GooglePlay active
        SENDER_ID = Constants.PROJECT_ID;
        gcm = GoogleCloudMessaging.getInstance(context);
        regid = null;
    }

    public void bindService(){
        if(!isBound){
            Intent intent2 = new Intent(context, HttpsBackgroundService.class);
            context.bindService(intent2, mConnection, context.BIND_AUTO_CREATE);
        }
    }

    public void stopBind(){
        if(isBound){
            context.unbindService(mConnection);
            isBound=false;
        }
    }



    private void createTestChats(){


        if(!database.isChatDBExisting()) {
            database.addChatToDB("Timo","+491774964208",false);
            database.addChatToDB("Jan Niklas", "+49017647736901",false);
            database.addChatToDB("Nicolas", "+49017661354169",false);
            database.addChatToDB("Noli", "+49017682541133",false);
            database.addChatToDB("Nils","+491727500917",false);
            database.addChatToDB("Marssenger Gruppe","54ca753b294895fe6abcfd4e",true);
            for(Chat chat : database.getChats()){
                database.addMessageToDB(chat.getMessageTableId(),".msg",0,0,0);
            }


        }







    }

    @Override
    public String getRegid(){return regid;}

    public boolean isRegistered(){
        boolean registered = false;

        final SharedPreferences prefs = context.getSharedPreferences("Account", context.MODE_PRIVATE);
        String myPhoneNumber = prefs.getString(PROPERTY_PHONE_NUMBER, "");

        Log.d("Preferences", myPhoneNumber);

        if(myPhoneNumber.isEmpty()){
            return false;
        }

        return true;
    }

    public String getMyNumber(){
        final SharedPreferences prefs = context.getSharedPreferences("Account", context.MODE_PRIVATE);
        String myPhoneNumber = prefs.getString(PROPERTY_PHONE_NUMBER, "");

        if(myPhoneNumber.isEmpty()){
            return "";
        }
        return myPhoneNumber;
    }

    public String getMyEMail(){
        final SharedPreferences prefs = context.getSharedPreferences("Account", context.MODE_PRIVATE);
        String myEMail = prefs.getString(PROPERTY_E_MAIL, "");

        if(myEMail.isEmpty()){
            return "";
        }
        return myEMail;
    }

    public void storePhoneNumber(String number){
        final SharedPreferences prefs = context.getSharedPreferences("Account", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_PHONE_NUMBER, number);
        editor.apply();
    }


    @Override
    public void cancelNotification(){
        //if there is a Notification from GCM, cancel.
        NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
        mService.clearNotification();
    }

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






    @Override
    public MainInteractorImpl get() {
        return this;
    }

    @Override
    public boolean connectionEstablished() {
        return connected;
    }

    @Override
    public void buildConnection() {
        connected=false;
    }



    private void test(CharSequence charSequence){
        Toast.makeText(context,charSequence,Toast.LENGTH_SHORT).show();
    }

}
