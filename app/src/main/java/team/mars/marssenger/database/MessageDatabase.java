package team.mars.marssenger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import team.mars.marssenger.datatype.Message;

/**
 * Created by Kern on 03.12.2014.
 */
public class MessageDatabase {

    // Database fields
    private SQLiteDatabase database;
    private SQLiteManager dbHelper;
    private String[] allColumnsMessage = { SQLiteManager.COLUMN_MESSAGES_ID,SQLiteManager.COLUMN_MESSAGES_SENDER, SQLiteManager.COLUMN_MESSAGES_MSG,SQLiteManager.COLUMN_MESSAGES_RECEIVER, SQLiteManager.COLUMN_MESSAGES_TIME };

    public MessageDatabase(Context context) {
        dbHelper = new SQLiteManager(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Message createMessage(String sender,String message,String reciver,String timestamp) {
        ContentValues values = new ContentValues();
        values.put(SQLiteManager.COLUMN_MESSAGES_SENDER, sender);
        values.put(SQLiteManager.COLUMN_MESSAGES_MSG, message);
        values.put(SQLiteManager.COLUMN_MESSAGES_RECEIVER, reciver);
        values.put(SQLiteManager.COLUMN_MESSAGES_TIME, timestamp);
        long insertId = database.insert(SQLiteManager.TABLE_MESSAGES, null, values);
        System.out.println(insertId);
        Cursor cursor = database.query(SQLiteManager.TABLE_MESSAGES,
                allColumnsMessage, SQLiteManager.COLUMN_MESSAGES_ID + " = " + insertId, null,
                null, null, null);

        cursor.moveToFirst();
        Message newMessage = cursorToMessage(cursor);
        cursor.close();
        return newMessage;
    }



    public void deleteMessage(Message message) {
        long id = message.getId();
        System.out.println("Message deleted with id: " + id);
        database.delete(SQLiteManager.TABLE_MESSAGES, SQLiteManager.COLUMN_MESSAGES_ID
                + " = " + id, null);
    }
    public void deleteMessage(long messageID) {
        System.out.println("Message deleted with id: " + messageID);
        database.delete(SQLiteManager.TABLE_MESSAGES, SQLiteManager.COLUMN_MESSAGES_ID
                + " = " + messageID, null);
    }

    public ArrayList<Message> getAllMessage() {
        ArrayList<Message> messages = new ArrayList<Message>();

        Cursor cursor =
                database.query(
                        SQLiteManager.TABLE_MESSAGES,
                        allColumnsMessage,
                        null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Message message = cursorToMessage(cursor);
            messages.add(message);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return messages;
    }

    private Message cursorToMessage(Cursor cursor) {
        Message message = new Message();
        try{
            message.setId(cursor.getLong(0));
            message.setSender(cursor.getString(1));
            message.setMessage(cursor.getString(2));
            message.setReciver(cursor.getString(3));
            message.setTime(cursor.getString(4));
        }catch (Exception ex){
            ex.printStackTrace();//TODO FIXEN
        }


        return message;
    }
}