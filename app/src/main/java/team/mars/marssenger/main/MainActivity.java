package team.mars.marssenger.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import team.mars.marssenger.R;
import team.mars.marssenger.register.RegisterActivity;


public class MainActivity extends ActionBarActivity implements MainView, RecyclerView.OnClickListener {



    //layout-attr

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;




    @Override
    protected void onResume() {
        super.onResume();
    }

    //attr
    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        mainPresenter=new MainPresenterImpl(this,this); //this - context, this - mainView


        setContentView(R.layout.activity_main);





        recyclerView=(RecyclerView) findViewById(R.id.main_listview);
        if (recyclerView!=null) {

            recyclerView.setAdapter(mainPresenter.getAdapter());

            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            test("recyclerview null");
        }

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            toolbar.setLogo(R.drawable.ic_launcher);
            toolbar.setTitle(R.string.app_name);
        } else {
            test("toolbar null");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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

    @Override
    public void onClick(View v) {
        mainPresenter.onChatClick(recyclerView.getChildPosition(v));
    }

    private void test(CharSequence charSequence) {
        Toast.makeText(this, charSequence, Toast.LENGTH_SHORT).show();
    }

    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        mainPresenter.onRegsiterReturn(requestCode,resultCode,data);
    }
    public void startRegisterationIntent(int REQUESTCODE){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent,REQUESTCODE);
    }
}
