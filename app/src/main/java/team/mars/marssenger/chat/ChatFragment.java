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

    public static ChatFragment getInstance(long chatId, MainInteractor mainInteractor){
        ChatFragment c=new ChatFragment();
        c.setChat(chatId);
        return c;
    }

    public void setInteractor(MainInteractor mainInteractor){this.chatPresenter.setInteractor(mainInteractor);}

    public void setChat(long chatId){this.chatPresenter.setChat(chatId);}

    public ChatFragment (){
        //create presenter which in turn creates the interactor
        chatPresenter=new ChatPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    public interface chatFragmentCallbacks{

    }
}

