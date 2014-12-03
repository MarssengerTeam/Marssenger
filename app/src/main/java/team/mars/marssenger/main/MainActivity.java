package team.mars.marssenger.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import team.mars.marssenger.R;


public class MainActivity extends Activity implements MainView, ListView.OnItemClickListener {

    //layout-attr
    private ListView listView;

    //attr
    private MainPresenter mainPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView) findViewById(R.id.main_listview);
        mainPresenter=new MainPresenterImpl(this);
        listView.setAdapter(mainPresenter.getAdapter(this));
        listView.setOnItemClickListener(this);
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

    }

    @Override
    public void setConnectionEstablished() {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

    }
}
