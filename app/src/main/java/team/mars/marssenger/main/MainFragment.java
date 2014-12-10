package team.mars.marssenger.main;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team.mars.marssenger.R;
import team.mars.marssenger.custom.CItemClickListener;
import team.mars.marssenger.datatype.Chat;


/**
 * Created by Kern on 09.12.2014.
 */
public class MainFragment extends Fragment implements MainView{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;
    private MainPresenter mainPresenter;
    private mainFragmentCallbacks listener;

    public static MainFragment getInstance(Context context, mainFragmentCallbacks listener){
        MainFragment m=new MainFragment();
        m.setContext(context);
        m.setListener(listener);
        return m;
    }

    public MainFragment (){

    }

    @Override
    public void setListener(mainFragmentCallbacks listener){this.listener=listener;}

    @Override
    public void setMainPresenter(MainPresenter mainPresenter) {
        this.mainPresenter=mainPresenter;
    }

    @Override
    public void setContext(Context context){this.context=context;}

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

    recyclerView=(RecyclerView) container.findViewById(R.id.main_listview);
    if (recyclerView!=null) {

        recyclerView.setAdapter(mainPresenter.getAdapter());

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        //react to touch input on this view
        recyclerView.addOnItemTouchListener(
                new CItemClickListener(
                        context.getApplicationContext(),
                        new CItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //do your stuff
                                if (mainPresenter!=null) {
                                    mainPresenter.onChatClick(view, position);
                                }
                            }
                        }
                )
        );

    }
        return inflater.inflate(
                R.layout.fragment_main, container, false);
    }
    @Override
    public void setConnectionFail() {

    }

    @Override
    public void setConnectionEstablished() {

    }

    @Override
    public void startRegisterationIntent(int REQUESTCODE) {

    }

    @Override
    public void openChat(long chatid) {
        listener.openChat(chatid);
    }

    public interface mainFragmentCallbacks{
        public void openChat(long chatid);
    }

}
