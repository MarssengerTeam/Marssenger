package team.mars.marssenger.main;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import team.mars.marssenger.R;


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
        setContentView(R.layout.activity_main);

        mainPresenter=new MainPresenterImpl(this,this); //this - context, this - mainView

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
}
