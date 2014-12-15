package team.mars.marssenger.main;

import android.content.Intent;
import android.view.View;

import team.mars.marssenger.custom.CChatListAdapter;
import team.mars.marssenger.custom.CListAdapter;
import team.mars.marssenger.datatype.Chat;

/**
 * Created by root on 03.12.14.
 */
public interface MainPresenter {

    //Methoden zur Steuerung des Interactors und der View

    public CListAdapter getAdapter();
    public CChatListAdapter getChatAdapter();
    public void onChatClick(View view,int position);
    public void onRegsiterReturn(int requestCode, int resultCode,Intent data);
    public void setToolbarText(String text);
    public void chatButtonSendPressed(Chat chat, String message);
    public void resetToolbarText();
}
