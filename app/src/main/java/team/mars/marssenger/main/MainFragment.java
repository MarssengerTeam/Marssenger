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
    private MainPresenter mainPresenter;

    public static MainFragment getInstance(MainPresenter mainPresenter){
        MainFragment m=new MainFragment();
        m.setPresenter(mainPresenter);
        return m;
    }

    public MainFragment (){

    }

    public void setPresenter(MainPresenter mainPresenter){this.mainPresenter=mainPresenter;}

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
                        getActivity().getApplicationContext(),
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
    public void startRegisterationIntent(int REQUESTCODE) {

    }

}
