package team.mars.marssenger.chat;

import team.mars.marssenger.custom.CChatListAdapter;

/**
 * Created by Nicolas on 23/01/2015.
 */
public interface ChatPresenter {
    public void chatButtonSendPressed(String message);
    public int getBottomPosition();
    public CChatListAdapter getChatAdapter();
    public void updateLayout();
}
