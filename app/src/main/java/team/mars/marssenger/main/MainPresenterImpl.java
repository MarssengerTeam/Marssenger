package team.mars.marssenger.main;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import team.mars.marssenger.R;

/**
 * Created by root on 03.12.14.
 */
public class MainPresenterImpl implements MainPresenter {

    //layout-attr

    //attr
    private MainView mainView;
    private MainInteractor mainInteractor;

    public MainPresenterImpl (MainView mainView){
        this.mainView=mainView;
        this.mainInteractor=new MainInteractorImpl();
        checkConnection();
    }

    @Override
    public ArrayAdapter<String> getAdapter(Context context){
        return new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                mainInteractor.getChatsList()
        );
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
