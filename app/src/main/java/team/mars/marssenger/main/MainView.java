package team.mars.marssenger.main;

import android.content.Intent;

/**
 * Created by root on 03.12.14.
 */
public interface MainView {

    //Methoden der View
    public void setConnectionFail();

    public void setConnectionEstablished();

    public void startRegisterationIntent(int REQUESTCODE);

    public void onActivityResult(int requestCode, int resultCode,Intent data);
}
