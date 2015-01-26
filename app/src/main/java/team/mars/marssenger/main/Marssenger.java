package team.mars.marssenger.main;

import android.app.Application;

import team.mars.marssenger.custom.CChatListAdapter;
import team.mars.marssenger.database.MessageDatabase;

/**
 * Created by Jan-Niklas on 26.01.2015.
 */
public class Marssenger extends Application {
    public static MainInteractor mainInteractor;
    public static CChatListAdapter cChatListAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
