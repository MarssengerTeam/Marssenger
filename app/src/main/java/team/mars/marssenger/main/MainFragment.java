package team.mars.marssenger.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import team.mars.marssenger.R;
import team.mars.marssenger.custom.CItemClickListener;


/**
 * Created by Kern on 09.12.2014.
 */
public class MainFragment extends Fragment implements MainView {
    private RecyclerView recyclerView;
    private MainPresenter mainPresenter;

    public static MainFragment getInstance(MainPresenter mainPresenter){
        MainFragment m=new MainFragment();
        m.setPresenter(mainPresenter);
        return m;
    }

    public MainFragment (){}

    public void setPresenter(MainPresenter mainPresenter){this.mainPresenter=mainPresenter;}

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View root=inflater.inflate(
                R.layout.fragment_main, container, false);
        recyclerView=(RecyclerView) root.findViewById(R.id.main_listview);

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        super.onViewCreated(view,savedInstanceState);

        //set layoutmanager
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(
                getActivity().getApplicationContext(),
                //2,
                GridLayoutManager.VERTICAL,
                false
        );
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //set adapter
        recyclerView.setAdapter(mainPresenter.getAdapter());

        //set itemtouchlistener
        recyclerView.addOnItemTouchListener(
                new CItemClickListener(
                        getActivity().getApplicationContext(),
                        new CItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //do your stuff
                                if (mainPresenter != null) {
                                    mainPresenter.onChatClick(view, position);
                                } else {
                                    test("mainPresenter null");
                                }
                            }
                        }
                )
        );
    }

    @Override
    public void startRegisterationIntent(int REQUESTCODE) {

    }

    private void test(CharSequence charSequence){
        Toast.makeText(getActivity().getApplicationContext(),charSequence,Toast.LENGTH_SHORT).show();
    }

}
