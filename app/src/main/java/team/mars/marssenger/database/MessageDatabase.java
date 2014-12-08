package team.mars.marssenger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.datatype.Message;

/**
 * Created by Kern on 03.12.2014.
 */
public class MessageDatabase {
    private SQLiteDatabase database;
    private SQLiteManager dbHelper;
    private String[] allColumnsMessage = { SQLiteManager.COLUMN_MESSAGES_ID, SQLiteManager.COLUMN_MESSAGES_MSG, SQLiteManager.COLUMN_MESSAGES_SENDER, SQLiteManager.COLUMN_MESSAGES_TIME, SQLiteManager.COLUMN_MESSAGES_READ};

    public MessageDatabase(Context context) {
        dbHelper = new SQLiteManager(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Message createMessage(String message,int isSender,int chatID,int read) {

        ContentValues values = new ContentValues();
        values.put(SQLiteManager.COLUMN_MESSAGES_MSG, message);
        values.put(SQLiteManager.COLUMN_MESSAGES_SENDER, isSender);
        values.put(SQLiteManager.COLUMN_MESSAGES_TIME, getDate());
        values.put(SQLiteManager.COLUMN_MESSAGES_READ, read);
        long insertId = database.insert(SQLiteManager.TABLE_MESSAGES_PREFIX+chatID, null, values);
        Cursor cursor = database.query(SQLiteManager.TABLE_MESSAGES_PREFIX+chatID,
                allColumnsMessage, SQLiteManager.COLUMN_MESSAGES_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Message newMessage = cursorToMessage(cursor,chatID);
        cursor.close();
        return newMessage;
    }

    public void deleteMessage(Chat chat) {
        System.out.println("Message deleted with id: " + chat.getMessageTableID());
        database.delete(SQLiteManager.TABLE_MESSAGES_PREFIX+chat.getMessageTableID(), SQLiteManager.COLUMN_MESSAGES_ID
                + " = " +chat.getMessageTableID(), null);
    }


    public ArrayList<Message> getAllMessageFromChat(Chat chat) {
        ArrayList<Message> messages = new ArrayList<Message>();
        Cursor cursor =
                database.query(
                        SQLiteManager.TABLE_MESSAGES_PREFIX+chat.getMessageTableID(),
                        allColumnsMessage,
                        null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Message message = cursorToMessage(cursor,chat.getId());
            messages.add(message);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return messages;
    }

    private Message cursorToMessage(Cursor cursor,long chatID) {
        Message message = new Message();
        try{
            message.setId(cursor.getLong(0));
            message.setMessage(cursor.getString(1));
            message.setSender(cursor.getInt(2));
            message.setTime(cursor.getLong(3));
            message.setRead(cursor.getInt(4));
            message.setChatID(chatID);
        }catch (Exception ex){
            ex.printStackTrace();
        }


        return message;
    }
    long getDate(){
        return new Date().getTime();
    }
}