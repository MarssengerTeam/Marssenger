package team.mars.marssenger.main;

import android.content.Intent;
import android.view.MenuItem;

import team.mars.marssenger.custom.CListAdapter;

/**
 * Created by root on 03.12.14.
 */
public interface MainPresenter {

    //Methoden zur Steuerung des Interactors und der View

    public CListAdapter getAdapter();
    public void onChatClick(int position);
    public boolean menuItemSelected(MenuItem item);
    public void onRegsiterReturn(int requestCode, int resultCode,
                                 Intent data);

}
