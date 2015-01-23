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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import team.mars.marssenger.R;
import team.mars.marssenger.custom.CChatListAdapter;

/**
 * Created by Kern on 09.12.2014.
 */
public class ChatFragment extends Fragment implements
        ChatView,
        View.OnClickListener{

    private RecyclerView recyclerView;
    private ChatPresenter chatPresenter;
    private EditText chat_input_edittext;
    private Button sendButton;

    //adapter and layoutmanager
    private CChatListAdapter adapter;
    private RecyclerView.LayoutManager manager;

    public static ChatFragment getInstance(ChatPresenter chatPresenter){
        ChatFragment c=new ChatFragment();
        c.setChatPresenter(chatPresenter);
        return c;
    }

    public ChatFragment (){}

    public void setChatPresenter(ChatPresenter chatPresenter){this.chatPresenter=chatPresenter;}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_chat, container, false);
        //get reference to recyclerview
        chat_input_edittext = (EditText) layout.findViewById(R.id.chat_input_edittext);
        sendButton = (Button) layout.findViewById(R.id.chat_send_button);
        recyclerView = (RecyclerView) layout.findViewById(R.id.chat_listview);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        //set layoutmanager
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //set adapter
        this.adapter=chatPresenter.getChatAdapter();
        recyclerView.setAdapter(adapter);
        scrollToBottom();
    }

    @Override
    public void updateContent(CChatListAdapter adapter){
        this.adapter=adapter;
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void test(CharSequence charSequence){
        Toast.makeText(getActivity().getApplicationContext(),charSequence,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void scrollToBottom() {
        recyclerView.scrollToPosition(chatPresenter.getBottomPosition());
    }

    @Override
    public void smoothScrollToBottom() {
        recyclerView.smoothScrollToPosition(chatPresenter.getBottomPosition());
    }

    @Override
    public void onClick(View v) {

    }
}

