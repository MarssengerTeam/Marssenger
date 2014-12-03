package team.mars.marssenger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import team.mars.marssenger.datatype.Chat;


/**
 * Created by Kern on 03.12.2014.
 */
public class ChatDatabase {

    // Database fields
    private SQLiteDatabase database;
    private SQLiteManager dbHelper;
    private String[] allColumnsChat = { SQLiteManager.COLUMN_CHAT_ID,SQLiteManager.COLUMN_CHAT_NAME, SQLiteManager.COLUMN_CHAT_NUMBER };

    public ChatDatabase(Context context) {
        dbHelper = new SQLiteManager(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Chat createChat(String name,String number) {
        ContentValues values = new ContentValues();
        values.put(SQLiteManager.COLUMN_CHAT_NAME, name);
        values.put(SQLiteManager.COLUMN_CHAT_NUMBER, number);
        long insertId = database.insert(SQLiteManager.TABLE_CHAT, null,values);
        Cursor cursor = database.query(SQLiteManager.TABLE_CHAT,
                allColumnsChat, SQLiteManager.COLUMN_CHAT_NAME + " = " + insertId, null,
                null, null, null);

        cursor.moveToFirst();
        Chat newChat = cursorToChat(cursor);
        cursor.close();
        //TODO UPDATE CHATS !
        return newChat;
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
        // make sure to close the cursor
        cursor.close();
        return chats;
    }

    private Chat cursorToChat(Cursor cursor) {
        Chat chat = new Chat();
        try{
            chat.setId(cursor.getLong(0));
            chat.setName(cursor.getString(1));
            chat.setNumber(cursor.getString(2));
        }catch (Exception ex){
            ex.printStackTrace();//TODO FIXEN
        }


        return chat;
    }
}