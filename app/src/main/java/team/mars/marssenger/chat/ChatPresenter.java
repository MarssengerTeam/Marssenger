package team.mars.marssenger.chat;

import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.main.MainInteractor;

/**
 * Created by Nicolas on 09/12/2014.
 */
public interface ChatPresenter {

    public void setChat(long chatId);

    public void setInteractor(MainInteractor interactor);
}
