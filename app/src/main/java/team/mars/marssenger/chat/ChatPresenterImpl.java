package team.mars.marssenger.chat;

/**
 * Created by Nicolas on 10/12/2014.
 */
public class ChatPresenterImpl implements ChatPresenter {

    private ChatView chatView;
    private ChatInteractor chatInteractor;

    private long chatId;

    public ChatPresenterImpl(ChatView chatView){
        this.chatView=chatView;
        this.chatInteractor=new ChatInteractorImpl();
    }

    @Override
    public void setChat(long chatId) {
        this.chatId=chatId;
        //update content if necessary
    }
}
