package team.mars.marssenger.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import team.mars.marssenger.R;
import team.mars.marssenger.register.RegisterActivity;


public class MainActivity extends ActionBarActivity implements
            MainView,
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

        if (Build.VERSION.SDK_INT ==Build.VERSION_CODES.LOLLIPOP) {
            colorActionBar();
        }

        mainPresenter=new MainPresenterImpl(this,this); //this - context, this - mainView

        setContentView(R.layout.activity_main);

        recyclerView=(RecyclerView) findViewById(R.id.main_listview);
        if (recyclerView!=null) {

            recyclerView.setAdapter(mainPresenter.getAdapter());

            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            //react to touch input on this view

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
