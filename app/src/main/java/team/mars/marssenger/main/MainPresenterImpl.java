package team.mars.marssenger.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import team.mars.marssenger.R;
import team.mars.marssenger.custom.CListAdapter;

/**
 * Created by root on 03.12.14.
 */
public class MainPresenterImpl implements MainPresenter {





    //layout-attr

    //attr
    private MainView mainView;
    private MainInteractor mainInteractor;
    private Context context;
    private CListAdapter cListAdapter;
    private final int REGISTER_REQUEST_CODE = 01000;

    public MainPresenterImpl (MainView mainView,Context context){
        this.context=context;
        this.mainView=mainView;
        this.mainInteractor=new MainInteractorImpl(context);
        if(mainInteractor.checkPlayServices()){
            if (mainInteractor.getRegid().isEmpty()) {
                mainView.startRegisterationIntent(REGISTER_REQUEST_CODE);
                mainInteractor.registerInBackground();
            }
        }
    }



    @Override
    public CListAdapter getAdapter(){
        cListAdapter=new CListAdapter(context, mainInteractor.getChatDatabase());
        return cListAdapter;
    }

    @Override
    public void onChatClick(int position) {
        //TODO check which chat it is and start chatactivity
        test(String.valueOf(cListAdapter.getItemId(position)));
    }

    @Override
    public boolean menuItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:

                return true;
            case R.id.action_new_message:

                return true;
            case R.id.action_search:

                return true;

            default:break;
        }

        return false;
    }

    @Override
    public void onRegsiterReturn(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGISTER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String number = data.getStringExtra("number");
                mainInteractor.storeRegistrationId(context,number);
            }
        }
    }


    //displays toast with given text
    private void test(CharSequence charSequence) {
        Toast.makeText(context, charSequence, Toast.LENGTH_SHORT).show();
    }


}
