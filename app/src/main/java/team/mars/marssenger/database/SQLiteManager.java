package team.mars.marssenger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Kern on 03.12.2014.
 */
public class SQLiteManager extends SQLiteOpenHelper {

   SQLiteDatabase database;

    public static final String COLUMN_MESSAGES_ID ="_id";
    public static final String COLUMN_MESSAGES_MSG = "message";
    public static final String COLUMN_MESSAGES_TIME = "time";
    public static final String COLUMN_MESSAGES_SENDER= "isSender";
    public static final String COLUMN_MESSAGES_READ= "read";


    public static final String TABLE_MESSAGES_PREFIX= "messagesforchat";


    public static final String TABLE_CHAT = "chats";
    public static final String COLUMN_CHAT_ID = "_id";
    public static final String COLUMN_CHAT_NAME = "name";
    public static final String COLUMN_CHAT_MESSAGENUMBER = "number";
    public static final String COLUMN_CHAT_RECEIVER = "receiver";

    private static final String DATABASE_NAME = "chats.db";
    private static final int DATABASE_VERSION = 3;

    // Database creation sql statement
    private static final String DATABASE_CREATE_TABLE_CHAT = "create table "
            + TABLE_CHAT + "("
            + COLUMN_CHAT_ID
            + " integer primary key autoincrement, "
            + COLUMN_CHAT_NAME
            + " text not null,"
            + COLUMN_CHAT_MESSAGENUMBER
            + " text not null, "
            + COLUMN_CHAT_RECEIVER
            + " text not null"
            + ");";
    private static final String DATABASE_CREATE_TABLE_MESSAGE_COLUMS =
             COLUMN_MESSAGES_ID
            + "( integer primary key autoincrement, "
            + COLUMN_MESSAGES_MSG
            + " text not null,"
            + COLUMN_MESSAGES_SENDER
            + " INTEGER, "
            + COLUMN_MESSAGES_TIME
            + " text not null, "
            + COLUMN_MESSAGES_READ
            + " integer);";

    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        this.database=database;
        this.database.execSQL(DATABASE_CREATE_TABLE_CHAT);
    }
    public void newMessageDatabase(int messageDatabaseID){
        if(database!=null) {
            database.execSQL("create table messagesforchat" + messageDatabaseID + " " + DATABASE_CREATE_TABLE_MESSAGE_COLUMS);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteManager.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES); TODO ALLE LÖSCHEN
        onCreate(db);
    }

}