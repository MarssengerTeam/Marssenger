package team.mars.marssenger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.datatype.GroupChat;
import team.mars.marssenger.datatype.Message;
import team.mars.marssenger.datatype.SingleChat;
import team.mars.marssenger.util.QuicksortMessages;


/**
 * Created by Kern on 03.12.2014.
 */
public class ChatDatabase {

    // Database fields
    private SQLiteDatabase database;
    private SQLiteManager dbHelper;
    private String[] allColumnsChat = { SQLiteManager.COLUMN_CHAT_ID, SQLiteManager.COLUMN_CHAT_NAME, SQLiteManager.COLUMN_CHAT_MESSAGENUMBER,SQLiteManager.COLUMN_CHAT_RECEIVER };
    private MessageDatabase messageDatabase;

    public ChatDatabase(Context context,MessageDatabase messageDatabase) {
        dbHelper = new SQLiteManager(context);
        this.messageDatabase=messageDatabase;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Chat createChat(String name,String receiver) {
        int messageDBID= this.getAllChat().size();
        dbHelper.newMessageDatabase(messageDBID);
        ContentValues values = new ContentValues();
        values.put(SQLiteManager.COLUMN_CHAT_NAME, name);
        values.put(SQLiteManager.COLUMN_CHAT_MESSAGENUMBER, messageDBID);
        values.put(SQLiteManager.COLUMN_CHAT_RECEIVER, receiver);
        long insertId = database.insert(SQLiteManager.TABLE_CHAT, null,values);
        Cursor cursor = database.query(SQLiteManager.TABLE_CHAT,
                allColumnsChat, SQLiteManager.COLUMN_CHAT_NAME + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Chat newChat = cursorToChat(cursor);
        cursor.close();

        return newChat;
    }

    public Message getLastMessage(Chat chat){
        ArrayList<Message> messages =  messageDatabase.getAllMessageFromChat(chat);
        if(messages.size()!=0) {
            Message m = messages.get(messages.size()-1);
            return m;
        } else{
            return null;
        }
    }
    int getDatabaseChatSize(){
        return 1;
    }
    public int isChatExisting(String reciever) {
        String[] databasearray = {reciever};
        String[] Collum = {SQLiteManager.COLUMN_CHAT_ID};


        Cursor cursor = database.query(SQLiteManager.TABLE_CHAT, Collum, SQLiteManager.COLUMN_CHAT_RECEIVER + " = " + reciever, null, null, null, null);

        try {
            cursor.moveToFirst();
            Chat newChat = cursorToChat(cursor);
            cursor.close();
            if (cursor.getColumnCount() == 0) {
                Log.e("ChatDatabase", "Debug: Kein chat von " + reciever);
                return -1;
            } else {
                return (int) newChat.getId();
            }
        } catch (Exception ex) {
            ex.printStackTrace();//TODO FIXEN
        }
        return -2;
    }

    public int getUnreadMessages(Chat chat){
        int i= 0;
        for(Message m: messageDatabase.getAllMessageFromChat(chat)) {
            if(!m.isRead()){
                i++;
            }
        }
        return i;
    }

    public void deleteChat(long chatID) {
        System.out.println("Chat deleted with id: " + chatID);
        database.delete(SQLiteManager.TABLE_CHAT, SQLiteManager.COLUMN_CHAT_ID
                + " = " + chatID, null);
        //TODO UPDATE CHATS !
    }

    public void deleteChat(Chat chat) {
        long id = chat.getId();
        System.out.println("Chat deleted with id: " + id);
        database.delete(SQLiteManager.TABLE_CHAT, SQLiteManager.COLUMN_CHAT_ID
                + " = " + id, null);
        //TODO UPDATE CHATS !
    }

    public ArrayList<Chat> getAllChat() {
        ArrayList<Chat> chats = new ArrayList<Chat>();
        Cursor cursor =
                database.query(
                        SQLiteManager.TABLE_CHAT,
                        allColumnsChat,
                        null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Chat chat = cursorToChat(cursor);
            chats.add(chat);
            cursor.moveToNext();
        }
        cursor.close();
        return chats;
    }
    public ArrayList<Chat> getAllChatByTime() {
        ArrayList<Chat> chats =  getAllChat();
        ArrayList<Message> lastMessages= new ArrayList<>();
        for(Chat chat:chats){
            if(getLastMessage(chat)!=null){
                lastMessages.add(getLastMessage(chat));}
        }
        QuicksortMessages qsm = new QuicksortMessages();
        ArrayList<Chat> csorted = new ArrayList<>();
        ArrayList<Message>  msorted=  qsm.sortMessages(lastMessages);
        for(Message m:msorted){
            csorted.add(0,chats.get((int)m.getChatID()-1));
        }
        return csorted;
    }

        private Chat cursorToChat(Cursor cursor) {

        Chat chat;
        try {
        String reciever = cursor.getString(3);

        if(reciever.startsWith("g:")){
           chat = new GroupChat();
        }else{
           chat = new SingleChat();
        }

                chat.setId(cursor.getLong(0));
                chat.setName(cursor.getString(1));
                chat.setMessageTableId(cursor.getLong(2));
                chat.setReceiver(reciever);
            } catch (Exception ex) {
                ex.printStackTrace();//TODO FIXEN
            chat = new SingleChat();
            }


        return chat;
    }
}