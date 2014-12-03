package team.mars.marssenger.main;

import java.util.ArrayList;

/**
 * Created by root on 03.12.14.
 */
public interface MainInteractor {

    //Methoden für den Zugriff auf die Daten
    public ArrayList<String> getChatsList();

    public boolean connectionEstablished();

    public void buildConnection();

    public void openDB();
}
