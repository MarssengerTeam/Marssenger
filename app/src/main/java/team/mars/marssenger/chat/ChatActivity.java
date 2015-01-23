package team.mars.marssenger.chat;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import team.mars.marssenger.R;
import team.mars.marssenger.custom.CChatListAdapter;
import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.main.MainInteractor;

public class ChatActivity extends ActionBarActivity implements ChatPresenter {

    private CChatListAdapter cChatListAdapter;
    private MainInteractor mainInteractor;
    private Chat chat;
    private ChatView chatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorActionBar();
        }

        //get chat
        this.chat=(Chat) getIntent().getExtras().getParcelable(Chat.CHAT);
        test(chat.getName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void test(CharSequence charSequence){
        Toast.makeText(getApplicationContext(),charSequence,Toast.LENGTH_SHORT).show();
    }

    private void replaceContainer(Fragment fragment) {
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
            FragmentTransaction lollioptransaction = getLollipopTransaction(transaction);
            lollioptransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            lollioptransaction.replace(R.id.chat_container, fragment);
            lollioptransaction.addToBackStack(null);
            lollioptransaction.commit();
        }else{
            transaction.replace(R.id.chat_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    //TODO rausfinden warum diese Methode aufgerufen werden soll
    private FragmentTransaction getLollipopTransaction(FragmentTransaction transaction){
        //   transaction.addSharedElement(mainFragment.getView().findViewById(R.id.listitem_name), "listitem_name");
        return transaction;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void colorActionBar() {getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));}

    //ChatView methods
    @Override
    public void chatButtonSendPressed(String message) {

        /*
        mService.sendMessage(this.chat.getReciever()[0], message, "12345");
         */
        test("omg the button was PRESSED!");
    }

    @Override
    public int getBottomPosition() {
        return cChatListAdapter.getItemCount()-1;
    }

    @Override
    public CChatListAdapter getChatAdapter() {
        cChatListAdapter=new CChatListAdapter(mainInteractor.getMessageDataBase(),this.chat);
        return cChatListAdapter;
    }
}
