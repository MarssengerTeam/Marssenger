package team.mars.marssenger.chat;

import team.mars.marssenger.main.MainInteractor;

/**
 * Created by Nicolas on 10/12/2014.
 */
public class ChatPresenterImpl implements ChatPresenter {

    private ChatView chatView;
    private MainInteractor mainInteractor;

    private long chatId;

    public ChatPresenterImpl(ChatView chatView){
        this.chatView=chatView;

    }

    @Override
    public void setChat(long chatId) {
        this.chatId=chatId;
        //update content if necessary
    }

    @Override
    public void setInteractor(MainInteractor interactor) {
        this.mainInteractor=interactor;
    }
}
