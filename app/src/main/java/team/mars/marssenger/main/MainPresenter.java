package team.mars.marssenger.main;

import android.content.Context;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import team.mars.marssenger.custom.CListAdapter;

/**
 * Created by root on 03.12.14.
 */
public interface MainPresenter {

    //Methoden zur Steuerung des Interactors und der View

    public CListAdapter getAdapter(Context context);
    public void onChatClick(AdapterView<?> adapterView, View view, int position, long id);
    public void checkConnection();
    public boolean menuItemSelected(MenuItem item);

}
