package team.mars.marssenger.chat;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
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
