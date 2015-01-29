package team.mars.marssenger.database;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.datatype.Message;

/**
 * Created by Kern on 28.01.2015.
 */
public class DatabaseWrapper {
    private MessageDatabase mDB;
    private ChatDatabase cDB;

    private static boolean hasChatChanged = true;
    private static boolean hasMessageChanged = true;

    private ArrayList<Chat> chats;
    private ArrayList<Chat> chatsByTime;
    private ArrayList<ArrayList<Message>> messages=new ArrayList<>();
    public DatabaseWrapper(Context context){
        mDB=new MessageDatabase(context);
        cDB=new ChatDatabase(context,mDB);
        openDB();
    }

    //READ FROM DB

    public void loadDB(){
        chats=cDB.getAllChat();
        chatsByTime=cDB.getAllChatByTime();
        for(Chat chat: chats){
            messages.add(mDB.getAllMessageFromChat(chat));
        }
        hasMessageChanged=false;hasChatChanged=false;
    }

    public ArrayList<Chat> getChats(){
        if(hasChatChanged||hasMessageChanged){
            loadDB();
        }
        return chats;
    }
    public ArrayList<Chat> getChatsByTime(){
      return cDB.getAllChatByTime();
    }
    public ArrayList<Message> getMessagesFromChatID(int id){
        if(hasChatChanged||hasMessageChanged){
            loadDB();
        }
        Chat search=null;
        for(Chat chat: chats){
            if(chat.getId()==id){
                search=chat;
            }
        }
        if(search==null){
            Log.e("DatabaseWrapper","Chat with ID "+id+" could not be found!");
            return null;
        }
        return mDB.getAllMessageFromChat(search);
    }
    public ArrayList<Message> getMessagesFormChat(Chat chat){
       return mDB.getAllMessageFromChat(chat);
    }
    public boolean isChatDBExisting(){
        if(cDB.getAllChat().size()==0){
            return false;
        }
        return true;
    }
    public int getUnreadMessages(Chat chat){
        return  cDB.getUnreadMessages(chat);

    }
    public Message getLastMessageFromChat(Chat chat){
        return cDB.getLastMessage(chat);
    }
    //WRITE TO DB

    public void addChatToDB(String name,String receiver,boolean isGroup){
        hasChatChanged=true;
        int isTypeGroup;
        if(isGroup){
            isTypeGroup=1;
        }else{
            isTypeGroup=0;
        }
        cDB.createChat(name,receiver,isTypeGroup);
    }

    public void addMessageToDB(long chatID,String msg,int isSender,int type, int read){
       hasMessageChanged=true;
       mDB.createMessage(msg,isSender,chatID,read,type);
    }
    public void setMessagesRead(Chat chat){
        hasMessageChanged=true;
        mDB.setAllMessagesRead(chat);
    }


    public void openDB() {
        mDB.open();
        cDB.open();
    }


    public void closeDB() {
        mDB.close();
        cDB.close();
    }




}
