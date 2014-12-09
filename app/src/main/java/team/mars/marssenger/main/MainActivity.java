package team.mars.marssenger.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import team.mars.marssenger.R;
import team.mars.marssenger.chat.ChatActivity;
import team.mars.marssenger.custom.CItemClickListener;
import team.mars.marssenger.register.RegisterActivity;


public class MainActivity extends ActionBarActivity implements MainView{

    //layout-attr



    @Override
    protected void onResume() {
        super.onResume();
    }

    //attr
    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (Build.VERSION.SDK_INT ==Build.VERSION_CODES.LOLLIPOP) {
            colorActionBar();
        }



        mainPresenter=new MainPresenterImpl(this,this); //this - context, this - mainView

        setContentView(R.layout.activity_main);


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void colorActionBar() {getWindow().setStatusBarColor(getResources().getColor(R.color.primary700));}

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return mainPresenter.menuItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void setConnectionFail() {
        test(getString(R.string.main_connection_failed));
    }

    @Override
    public void setConnectionEstablished() {
        test(getString(R.string.main_connection_established));
    }

    private void test(CharSequence charSequence) {
        Toast.makeText(this, charSequence, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mainPresenter.onRegsiterReturn(requestCode,resultCode,data);
    }

    @Override
    public void openChat(int chatID) {

        Intent chatIntent = new Intent(this, RegisterActivity.class);
        chatIntent.putExtra("CHAT_ID",chatID);
        try{
              startActivity(chatIntent);
        }catch(Exception e){

        }

    }


    @Override
    public void startRegisterationIntent(int REQUESTCODE){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent,REQUESTCODE);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        test("click");
        return mainPresenter.menuItemSelected(menuItem);
    }
}
