package team.mars.marssenger.chat;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TreeSet;

import team.mars.marssenger.R;
import team.mars.marssenger.custom.CChatListAdapter;
import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.datatype.Message;
import team.mars.marssenger.main.MainActivity;
import team.mars.marssenger.main.MainInteractor;

public class ChatActivity extends ActionBarActivity implements
        ChatPresenter,
        Toolbar.OnMenuItemClickListener
{

    //mvc
    private CChatListAdapter cChatListAdapter;
    private MainInteractor mainInteractor;

    private Chat chat;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.chat= (Chat) getIntent().getSerializableExtra(Chat.CHAT);
        this.mainInteractor= MainActivity.MAIN_INTERACTOR;

        setContentView(R.layout.activity_chat);

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null){
            toolbar.setTitle(chat.getName());
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            //menu
            toolbar.inflateMenu(R.menu.menu_main);
            toolbar.setOnMenuItemClickListener(this);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorActionBar();
        }

        mainInteractor.getMessageDataBase().setAllMessagesRead(chat);

        //TODO SEND TO SERVER MESSAGES READ

        //create fragment
        ChatFragment chatFragment=ChatFragment.getInstance(this); //ChatPresenter
        replaceContainer(chatFragment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                return true;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void test(CharSequence charSequence){
        Toast.makeText(getApplicationContext(),charSequence,Toast.LENGTH_SHORT).show();
    }

    private void replaceContainer(Fragment fragment) {
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.replace(R.id.chat_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void colorActionBar() {getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));}

    //ChatView methods
    @Override
    public void chatButtonSendPressed(String message) {
        mainInteractor.getMessageDataBase().createMessage(message,1,chat.getId()-1,1,0);
        ArrayList<Message> messages = mainInteractor.getMessageDataBase().getAllMessageFromChat(chat);
        cChatListAdapter.addMessage(messages.get(messages.size()-1));
        /*TODO figure out how to send a message
        mService.sendMessage(this.chat.getReciever()[0], message, "12345");
        */
    }

    @Override
    public int getBottomPosition() {
        return cChatListAdapter.getItemCount()-1;
    }

    @Override
    public CChatListAdapter getChatAdapter() {
        cChatListAdapter=new CChatListAdapter(mainInteractor.getMessageDataBase(),this.chat, getApplicationContext());
        return cChatListAdapter;
    }

    @Override
    public void updateLayout() {
        cChatListAdapter.updateLayout();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.action_search:

                return true;
            default:break;
        }
        return onOptionsItemSelected(menuItem);
    }
}
