package team.mars.marssenger.chat;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import team.mars.marssenger.R;
import team.mars.marssenger.main.MainPresenter;

/**
 * Created by Kern on 09.12.2014.
 */
public class ChatFragment extends Fragment implements ChatView {

    private RecyclerView recyclerView;
    private MainPresenter mainPresenter;

    public static ChatFragment getInstance(MainPresenter mainPresenter){
        ChatFragment c=new ChatFragment();
        c.setMainPresenter(mainPresenter);
        return c;
    }

    public ChatFragment (){}

    public void setMainPresenter(MainPresenter mainPresenter){this.mainPresenter=mainPresenter;}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_chat, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_chat, container, false);
        //get reference to recyclerview
        recyclerView = (RecyclerView) layout.findViewById(R.id.chat_listview);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        //set layoutmanager
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //set adapter
        recyclerView.setAdapter(mainPresenter.getChatAdapter());

    }

    private void test(CharSequence charSequence){
        Toast.makeText(getActivity().getApplicationContext(),charSequence,Toast.LENGTH_SHORT).show();
    }
}

