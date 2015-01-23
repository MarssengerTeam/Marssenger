package team.mars.marssenger.chat;

import team.mars.marssenger.custom.CChatListAdapter;

/**
 * Created by Nicolas on 09/12/2014.
 */
public interface ChatView {

    public void scrollToBottom();
    public void smoothScrollToBottom();

    public void updateContent(CChatListAdapter adapter);

    public void combineMessages(int [] positions);
}
