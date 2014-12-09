package team.mars.marssenger.main;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import team.mars.marssenger.R;
import team.mars.marssenger.custom.CItemClickListener;



/**
 * Created by Kern on 09.12.2014.
 */
public class MainFragment extends Fragment implements MainView, Toolbar.OnMenuItemClickListener{
    private RecyclerView recyclerView;
    private LayoutManager layoutManager;
    private Toolbar toolbar;
    private Context context;
    private MainInteractor mainInteractor;
    private MainPresenter mainPresenter;
    MainFragment(Context context, MainInteractor mainInteractor, MainPresenter mainPresenter){
        this.context=context;
        this.mainInteractor=mainInteractor;
        this.mainPresenter=mainPresenter;

    }


    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

    recyclerView=(RecyclerView) container.findViewById(R.id.main_listview);
    if (recyclerView!=null) {

        recyclerView.setAdapter(mainPresenter.getAdapter());

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //react to touch input on this view
        recyclerView.addOnItemTouchListener(
                new CItemClickListener(
                        context.getApplicationContext(),
                        new CItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //do your stuff
                                mainPresenter.onChatClick(view, position);
                            }
                        }
                )
        );

    } else {
        test("recyclerview null");
    }

    toolbar=(Toolbar) findViewById(R.id.toolbar);
    if (toolbar!=null){

        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        toolbar.setTitle(R.string.app_name);

        //menu
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(this);
    } else {
        test("toolbar null");
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
    public void openChat(int chatID) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }
}
