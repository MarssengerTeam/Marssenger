package team.mars.marssenger.chat;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team.mars.marssenger.R;
import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.main.MainInteractor;

/**
 * Created by Kern on 09.12.2014.
 */
public class ChatFragment extends Fragment implements ChatView {

    private ChatPresenter chatPresenter;
    private chatFragmentCallbacks listener;

    public static ChatFragment getInstance(long chatId, chatFragmentCallbacks listener){
        ChatFragment c=new ChatFragment();
        c.setChat(chatId);
        return c;
    }

    public void setListener(chatFragmentCallbacks listener){this.listener=listener;}

    public void setChat(long chatId){this.chatPresenter.setChat(chatId);}

    public ChatFragment (){
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    public interface chatFragmentCallbacks{

    }
}

