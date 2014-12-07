package team.mars.marssenger.main;

import android.content.Context;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import team.mars.marssenger.custom.CListAdapter;
import team.mars.marssenger.datatype.Chat;

/**
 * Created by root on 03.12.14.
 */
public interface MainPresenter {

    //Methoden zur Steuerung des Interactors und der View

    public CListAdapter getAdapter();
    public void onChatClick(int position);
    public boolean menuItemSelected(MenuItem item);

}
