package team.mars.marssenger.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

import team.mars.marssenger.R;
import team.mars.marssenger.chat.ChatFragment;
import team.mars.marssenger.communication.HttpsBackgroundService;
import team.mars.marssenger.custom.CChatListAdapter;
import team.mars.marssenger.custom.CListAdapter;
import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.settings.SettingsActivity;
import team.mars.marssenger.util.Constants;


public class MainActivity extends ActionBarActivity implements
            Toolbar.OnMenuItemClickListener,
            MainPresenter
{
    private Toolbar toolbar;

    private boolean mainFragmentActive;

    private CListAdapter cListAdapter;
    private CChatListAdapter cChatListAdapter;
    private final int REGISTER_REQUEST_CODE = 01000;

    private Chat chat;

    private MainFragment mainFragment;

    //mvc
    private MainInteractor mainInteractor;

    //HttpsService
    private HttpsBackgroundService mService;
    private boolean isBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            HttpsBackgroundService.myBinder binder = (HttpsBackgroundService.myBinder) service;
            mService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
            colorActionBar();
        }

        setContentView(R.layout.activity_main);

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null){

            setSupportActionBar(toolbar);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

            toolbar.setTitle(R.string.app_name);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));

            //menu
            toolbar.inflateMenu(R.menu.menu_main);
            toolbar.setOnMenuItemClickListener(this);
        } else {
            test("toolbar null");
        }

        mainInteractor=new MainInteractorImpl(this);
        if(mainInteractor.checkPlayServices()){
            if(!isSerivceRunning(HttpsBackgroundService.class)){
                Intent serviceIntent = new Intent(this, HttpsBackgroundService.class);
                serviceIntent.putExtra("senderID", Constants.PROJECT_ID);
                serviceIntent.putExtra("phoneNumber", "0157712345");
                serviceIntent.putExtra("email", "hurensohn@squad.com");
                serviceIntent.putExtra("digitCode", "010101");
                startService(serviceIntent);
            }else{
                Toast.makeText(getApplicationContext(), "Service already running", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.d("GCMundso", "Cry a lot!");
        }

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

        this.mainFragment=MainFragment.getInstance(this);//mainPresenter

        replaceContainer(mainFragment,false);
        mainFragmentActive=true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, HttpsBackgroundService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBound){
            unbindService(mConnection);
            isBound=false;
        }
    }

    public void replaceContainer(Fragment fragment,boolean addToBackStack) {
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
            FragmentTransaction lollioptransaction = getLollipopTransaction(transaction);
            lollioptransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            lollioptransaction.replace(R.id.container, fragment);
            lollioptransaction.addToBackStack(null);
            lollioptransaction.commit();
        }else{
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

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

    public FragmentTransaction getFragmentTransition(FragmentTransaction transaction){

        return transaction;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private FragmentTransaction getLollipopTransaction(FragmentTransaction transaction){


         //   transaction.addSharedElement(mainFragment.getView().findViewById(R.id.listitem_name), "listitem_name");

        return transaction;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return super.onCreateOptionsMenu(menu);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mainFragmentActive) {
                replaceContainer(mainFragment, false);
                mainFragmentActive = true;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.action_search:
                //TODO this is chat
                //only for testing, please changed if function is needed
                if(isBound) {
                    mService.sendMessage("0157712345", "test123", "12345");
                    Toast.makeText(getApplicationContext(), "isBound", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "not Bound", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_new_message:

                return true;
            default:break;
        }
        return false;
    }

    @Override
    public CListAdapter getAdapter(){
        cListAdapter=new CListAdapter(mainInteractor.getChatDatabase());
        return cListAdapter;
    }

    @Override
    public CChatListAdapter getChatAdapter(){
        cChatListAdapter=new CChatListAdapter(mainInteractor.getMessageDataBase(),this.chat);
        return cChatListAdapter;
    }

    @Override
    public void onChatClick(View view, int position) {
        chat=cListAdapter.getItem(position);
        setToolbarText(chat.getName());
        //create fragment and initiate it
        ChatFragment chatFragment=ChatFragment.getInstance(this); //mainPresenter
        replaceContainer(chatFragment,true);
        mainFragmentActive=false;
    }

    @Override
    public void onRegsiterReturn(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGISTER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String number = data.getStringExtra("number");
                //mainInteractor.storeRegistrationId(this,number);
            }
        }
    }

    @Override
    public void setToolbarText(String text) {
        toolbar.setTitle(text);
    }

    @Override
    public void chatButtonSendPressed(String message) {
        mService.sendMessage(this.chat.getReciever()[0], message, "12345");
    }

    @Override
    public void resetToolbarText() {
        toolbar.setTitle(R.string.app_name);
    }

    @Override
    public int getBottomPosition() {
        return cChatListAdapter.getItemCount()-1;
    }
}
