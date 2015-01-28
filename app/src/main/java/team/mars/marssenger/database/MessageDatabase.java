package team.mars.marssenger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.datatype.Message;
import team.mars.marssenger.datatype.PictureMessage;
import team.mars.marssenger.datatype.TextMessage;

/**
 * Created by Kern on 03.12.2014.
 */
public class MessageDatabase {
    private SQLiteDatabase database;
    private SQLiteManager dbHelper;
    private String[] allColumnsMessage = { SQLiteManager.COLUMN_MESSAGES_ID, SQLiteManager.COLUMN_MESSAGES_MSG, SQLiteManager.COLUMN_MESSAGES_SENDER, SQLiteManager.COLUMN_MESSAGES_TIME, SQLiteManager.COLUMN_MESSAGES_READ,SQLiteManager.COLUMN_CHAT_TYPE};

    public MessageDatabase(Context context) {
        dbHelper = new SQLiteManager(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Message createMessage(String message,int isSender, long chatID,int read,int type) {

        ContentValues values = new ContentValues();
        values.put(SQLiteManager.COLUMN_MESSAGES_MSG, message);
        values.put(SQLiteManager.COLUMN_MESSAGES_SENDER, isSender);
        values.put(SQLiteManager.COLUMN_MESSAGES_TIME, getDate());
        values.put(SQLiteManager.COLUMN_MESSAGES_READ, read);
        values.put(SQLiteManager.COLUMN_MESSAGES_TYPE, type);

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
        System.out.println("Message deleted with id: " + chat.getMessageTableId());
        database.delete(SQLiteManager.TABLE_MESSAGES_PREFIX+chat.getMessageTableId(), SQLiteManager.COLUMN_MESSAGES_ID
                + " = " +chat.getMessageTableId(), null);
    }

    public void setAllMessagesRead(Chat chat){
        database.execSQL("UPDATE "+SQLiteManager.TABLE_MESSAGES_PREFIX+chat.getMessageTableId()+" SET "+SQLiteManager.COLUMN_MESSAGES_READ+" = 1 WHERE "+SQLiteManager.COLUMN_MESSAGES_READ+ "= 0");
    }

    public ArrayList<Message> getAllMessageFromChat(Chat chat) {
        ArrayList<Message> messages = new ArrayList<Message>();
        Cursor cursor =
                database.query(
                        SQLiteManager.TABLE_MESSAGES_PREFIX+chat.getMessageTableId(),
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
        Message message;
        switch (cursor.getInt(5)){
            case 0:
                message = new TextMessage();
                break;
            case 1:
                message = new PictureMessage();
                break;
            default:
                message = null;
                Log.e("MessageDatabase","Message type wrong / not implemented yet");
                break;
        }

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