package team.mars.marssenger.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import team.mars.marssenger.R;
import team.mars.marssenger.chat.ChatActivity;
import team.mars.marssenger.communication.HttpsBackgroundService;
import team.mars.marssenger.custom.CListAdapter;
import team.mars.marssenger.custom.NewChatDialog;
import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.register.RegisterActivity;
import team.mars.marssenger.settings.SettingsActivity;
import team.mars.marssenger.util.Constants;


public class MainActivity extends ActionBarActivity implements
            Toolbar.OnMenuItemClickListener,
            MainPresenter {
    private Toolbar toolbar;

    private CListAdapter cListAdapter;
    private final int REGISTER_REQUEST_CODE = 01000;

    //mvc
    private MainInteractor mainInteractor;
    public static MainInteractor MAIN_INTERACTOR;



    //Controlling
    private  boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainInteractor=new MainInteractorImpl(this); //this -> MainPresenter
        if(!mainInteractor.isRegistered()){
            Intent regIntent = new Intent(this, RegisterActivity.class);
            startActivityForResult(regIntent,REGISTER_REQUEST_CODE);
        }else {
            isRegistered = true;
        }
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
            colorActionBar();
        }

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null){

            toolbar.setTitle(R.string.app_name);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));

            //menu
            toolbar.inflateMenu(R.menu.menu_main);
            toolbar.setOnMenuItemClickListener(this);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }


        mainInteractor=new MainInteractorImpl(this); //this -> MainPresenter
        //save mainInteractor in static reference so it can be accessed in other parts of the code
        //MainActivity.MAIN_INTERACTOR=this.mainInteractor;



        /*if(mainInteractor.checkPlayServices()){
            if(!isSerivceRunning(HttpsBackgroundService.class)){
                Intent serviceIntent = new Intent(this, HttpsBackgroundService.class);
                serviceIntent.putExtra("senderID", Constants.PROJECT_ID);
                serviceIntent.putExtra("phoneNumber", ma);
                serviceIntent.putExtra("email", "hurensohn@squad.com");
                serviceIntent.putExtra("digitCode", "010101"); //TODO Maybe / Maybe not
                startService(serviceIntent);
            }
        }else{
            Log.d("GCMundso", "Cry a lot!");
        }
        //TODO figure this out*/
        /*final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isBound) {
                    mService.sendMessage("0157712345", String.valueOf(new Random().nextInt(100)), "123456");
                    handler.postDelayed(this, 30000);
                }
            }
        }, 0);*/


        MainFragment mainFragment = MainFragment.getInstance(this);//this -> MainPresenter

        replaceContainer(mainFragment);

    }


    @Override
    protected void onStart(){
        super.onStart();
        ((Marssenger)Marssenger.getInstance()).setUserActive(true);
        if(isRegistered) {
            if(mainInteractor.checkPlayServices()){
                if(!isSerivceRunning(HttpsBackgroundService.class)){
                    Intent serviceIntent = new Intent(this, HttpsBackgroundService.class);
                    serviceIntent.putExtra("senderID", Constants.PROJECT_ID);
                    serviceIntent.putExtra("phoneNumber", mainInteractor.getMyNumber());
                    serviceIntent.putExtra("email", mainInteractor.getMyEMail());
                    serviceIntent.putExtra("digitCode", "010101"); //TODO Maybe / Maybe not
                    startService(serviceIntent);
                }
            }else{
                Log.d("GCMundso", "Cry a lot!");
            }
            mainInteractor.bindService();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        ((Marssenger)Marssenger.getInstance()).setUserActive(false);
        mainInteractor.stopBind();
    }

    private void replaceContainer(Fragment fragment) {
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private boolean isSerivceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void colorActionBar() {getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));}

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return onMenuItemClick(item) || super.onOptionsItemSelected(item);
    }

    private void test(CharSequence charSequence) {
        Toast.makeText(this, charSequence, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        onRegsiterReturn(requestCode, resultCode, data);
    }
    @Override
    public void onResume(){
        super.onResume();
        cListAdapter.updateCardView();
    }
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.action_search:
                ((Marssenger)Marssenger.getInstance()).getDatabase().deleteAllMessages();
                ((Marssenger)Marssenger.getInstance()).getDatabase().deleteAllChats();

                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_new_message:
                showNewChatDialog();
                return true;
            case R.id.action_read:
                startActivity(new Intent(this, ContactListingActivity.class));
                return true;
            default:break;
        }
        return false;
    }

    public void showNewChatDialog(){
        DialogFragment newFragment = new NewChatDialog();
        newFragment.show(getFragmentManager(), "missiles");
    }

    @Override
    public CListAdapter getAdapter(){
        cListAdapter=new CListAdapter();
        return cListAdapter;
    }

    @Override
    public void onChatClick(View view, int position) {
        //make intent and put serializable chat object into it
        Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Chat.CHAT, cListAdapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void onRegsiterReturn(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGISTER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("Register", "is Registered");
                isRegistered=true;
                String number = data.getStringExtra("number");
                Log.d("Register", number);
                mainInteractor.storePhoneNumber(number);
            }
        }
    }

    public MainInteractor getMainInteractor() {
        return mainInteractor;
    }


}
