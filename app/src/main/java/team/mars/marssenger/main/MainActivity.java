package team.mars.marssenger.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import team.mars.marssenger.R;


public class MainActivity extends Activity implements MainView, ListView.OnItemClickListener {

    //layout-attr
    private ListView listView;
    private TextView textView;

    //attr
    private MainPresenter mainPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=(ListView) findViewById(R.id.main_listview);
        textView=(TextView) findViewById(R.id.main_textview);

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
        textView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        textView.setText(getResources().getString(R.string.main_connection_failed));
        textView.setTextSize(getResources().getDimension(R.dimen.textview_textsize));
        textView.setTextColor(getResources().getColor(R.color.error));
    }

    @Override
    public void setConnectionEstablished() {
        textView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        mainPresenter.onChatClick(adapterView,view,position,id);
    }
}
