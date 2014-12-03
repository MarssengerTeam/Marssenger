package team.mars.marssenger.main;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import team.mars.marssenger.R;
import team.mars.marssenger.custom.CListAdapter;
import team.mars.marssenger.datatype.Chat;

/**
 * Created by root on 03.12.14.
 */
public class MainPresenterImpl implements MainPresenter {

    //layout-attr

    //attr
    private MainView mainView;
    private MainInteractor mainInteractor;
    private Context context;

    public MainPresenterImpl (MainView mainView,Context context){
        this.mainView=mainView;
        this.mainInteractor=new MainInteractorImpl(context);
        checkConnection();
    }

    @Override
    public CListAdapter getAdapter(){
        return new CListAdapter(context, mainInteractor.getChatsList());
    }

    @Override
    public void onChatClick(AdapterView<?> adapterView, View view, int position, long id) {
        //check which chat it is and start chatactivity
    }

    @Override
    public void checkConnection() {
        mainInteractor.buildConnection();
        if (mainInteractor.connectionEstablished()){
            mainView.setConnectionEstablished();
        } else {
            mainView.setConnectionFail();
        }
    }

    @Override
    public boolean menuItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:

                return true;
            case R.id.action_new_message:

                return true;
        }

        return false;
    }

}
