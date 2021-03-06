package team.mars.marssenger.chat;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import team.mars.marssenger.communication.HttpsBackgroundService;
import team.mars.marssenger.custom.CChatListAdapter;
import team.mars.marssenger.util.MessageStringValidater;

/**
 * Created by Kern on 09.12.2014.
 */
public class ChatFragment extends Fragment implements
        ChatView,
        View.OnClickListener{

    private RecyclerView recyclerView;
    private ChatPresenter chatPresenter;
    private EditText chatInput;
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
        chatInput = (EditText) layout.findViewById(R.id.chat_input_edittext);
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
        chatPresenter.updateLayout();

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
    public void smoothScrollToBottom(){
        recyclerView.smoothScrollToPosition(chatPresenter.getBottomPosition());
    }

    @Override
    public void onClick(View v) {

        //forward event to ChatPresenter with Text from EditText
        String input=chatInput.getText().toString();
        if(MessageStringValidater.isValid(input)){
            chatPresenter.chatButtonSendPressed(input);
            chatInput.setText("");
            smoothScrollToBottom();
        }else {
            test("Input not valid.");
        }
    }
}

