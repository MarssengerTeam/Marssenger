package team.mars.marssenger.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import team.mars.marssenger.R;
import team.mars.marssenger.register.RegisterActivity;


public class MainActivity extends ActionBarActivity implements MainView, RecyclerView.OnClickListener,
                                                                Toolbar.OnMenuItemClickListener
{

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

            recyclerView.setOnClickListener(this);
        } else {
            test("recyclerview null");
        }

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null){

            setSupportActionBar(toolbar);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

            toolbar.setTitle(R.string.app_name);

            //menu
            toolbar.inflateMenu(R.menu.menu_main);
            toolbar.setOnMenuItemClickListener(this);
        } else {
            test("toolbar null");
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mainPresenter.onRegsiterReturn(requestCode,resultCode,data);
    }

    @Override
    public void startRegisterationIntent(int REQUESTCODE){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent,REQUESTCODE);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return mainPresenter.menuItemSelected(menuItem);
    }
}
