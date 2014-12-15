package team.mars.marssenger.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import team.mars.marssenger.R;
import team.mars.marssenger.chat.ChatFragment;
import team.mars.marssenger.custom.CChatListAdapter;
import team.mars.marssenger.custom.CListAdapter;
import team.mars.marssenger.datatype.Chat;


public class MainActivity extends ActionBarActivity implements
            Toolbar.OnMenuItemClickListener,
            MainPresenter
{
    private Toolbar toolbar;

    private CListAdapter cListAdapter;
    private CChatListAdapter cChatListAdapter;
    private final int REGISTER_REQUEST_CODE = 01000;

    private Chat chat;

    private MainFragment mainFragment;

    //mvc
    private MainInteractor mainInteractor;

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
            if (mainInteractor.getRegid().isEmpty()) {
                mainInteractor.registerInBackground();
            }else{
                //TODO mainInteractor.registerAtServer("0157700003", "hurensohn@aufdeinem.grab", mainInteractor.getRegid(), "4321");
                //Log.d("GCMSending", mainInteractor.getRegid() + "Hat registriert");
            }
        }else{
            Log.d("GCMundso", "Cry a lot!");
        }

        this.mainFragment=MainFragment.getInstance(this);//mainPresenter

        replaceContainer(mainFragment);

    }

    public void replaceContainer(Fragment fragment) {
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void colorActionBar() {getWindow().setStatusBarColor(getResources().getColor(R.color.primary700));}

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
            replaceContainer(mainFragment);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.action_search:

                return true;
            case R.id.action_settings:

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
        replaceContainer(chatFragment);
    }

    @Override
    public void onRegsiterReturn(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGISTER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String number = data.getStringExtra("number");
                mainInteractor.storeRegistrationId(this,number);
            }
        }
    }

    @Override
    public void setToolbarText(String text) {
        toolbar.setTitle(text);
    }

    @Override
    public void chatButtonSendPressed(Chat chat, String message) {
        //TODO MY NUMBER HIER           vvv
        mainInteractor.sendMessage("0157700000", chat.getReciever(), message);
    }

    @Override
    public void resetToolbarText() {
        toolbar.setTitle(R.string.app_name);
    }
}
