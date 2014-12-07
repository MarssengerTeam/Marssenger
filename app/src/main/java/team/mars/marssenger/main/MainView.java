package team.mars.marssenger.main;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by root on 03.12.14.
 */
public interface MainView {

    //Methoden der View
    public void setConnectionFail();

    public void setConnectionEstablished();

    public void startRegisterationIntent(int REQUESTCODE);
}
