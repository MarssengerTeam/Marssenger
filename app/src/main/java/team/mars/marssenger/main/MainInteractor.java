package team.mars.marssenger.main;

import android.content.Context;

import java.util.ArrayList;

import team.mars.marssenger.database.ChatDatabase;
import team.mars.marssenger.database.MessageDatabase;
import team.mars.marssenger.datatype.Chat;

/**
 * Created by root on 03.12.14.
 */
public interface MainInteractor{
    public boolean connectionEstablished();
    public void buildConnection();

    public void bindService();
    public void stopBind();

    public boolean checkPlayServices(Context c);

    public void cancelNotification();

    public String getRegid();
    public boolean isRegistered();
    public String getMyNumber();
    public String getMyEMail();
    public void storePhoneNumber(String number);
    public void _Toast(Context c, String mess);


    public MainInteractorImpl get();
}
