package team.mars.marssenger.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import team.mars.marssenger.R;


public class MainActivity extends Activity implements MainView, RecyclerView.OnClickListener {

    //layout-attr
    private RecyclerView recyclerView;
    private TextView textView;
    private RecyclerView.LayoutManager layoutManager;

    //attr
    private MainPresenter mainPresenter;
    private MainInteractor mainInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=(RecyclerView) findViewById(R.id.main_listview);
        textView=(TextView) findViewById(R.id.main_textview);

        mainPresenter=new MainPresenterImpl(this,this);

        recyclerView.setAdapter(mainPresenter.getAdapter());
        recyclerView.setOnClickListener(this);

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
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
        recyclerView.setVisibility(View.GONE);

        textView.setText(getResources().getString(R.string.main_connection_failed));
        textView.setTextSize(getResources().getDimension(R.dimen.textview_textsize));
        textView.setTextColor(getResources().getColor(R.color.error));
    }

    @Override
    public void setConnectionEstablished() {
        textView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View view) {
        mainPresenter.onChatClick(view);
    }
}
