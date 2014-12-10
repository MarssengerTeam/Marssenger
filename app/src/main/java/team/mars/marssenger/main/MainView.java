package team.mars.marssenger.main;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import team.mars.marssenger.datatype.Chat;

/**
 * Created by root on 03.12.14.
 */
public interface MainView {

    //Methoden der View
    public void startRegisterationIntent(int REQUESTCODE);

    public void onActivityResult(int requestCode, int resultCode,Intent data);
}
