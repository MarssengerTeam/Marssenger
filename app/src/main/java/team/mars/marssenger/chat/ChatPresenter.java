package team.mars.marssenger.chat;

import java.io.File;

import team.mars.marssenger.custom.CChatListAdapter;

/**
 * Created by Nicolas on 23/01/2015.
 */
public interface ChatPresenter {
    public void chatButtonSendPressed(String message);
    public void imageSendToServer(File file);
    public int getBottomPosition();
    public CChatListAdapter getChatAdapter();
    public void updateLayout();
}
