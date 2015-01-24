package team.mars.marssenger.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kern on 03.12.2014.
 */
public class SQLiteManager extends SQLiteOpenHelper {


    public static final String COLUMN_MESSAGES_ID ="_id";
    public static final String COLUMN_MESSAGES_MSG = "message";
    public static final String COLUMN_MESSAGES_TIME = "time";
    public static final String COLUMN_MESSAGES_SENDER= "isSender";
    public static final String COLUMN_MESSAGES_READ = "read";
    public static final String COLUMN_MESSAGES_TYPE = "type";


    public static final String TABLE_MESSAGES_PREFIX= "messagesforchat";


    public static final String TABLE_CHAT = "chats";
    public static final String COLUMN_CHAT_ID = "_id";
    public static final String COLUMN_CHAT_NAME = "name";
    public static final String COLUMN_CHAT_MESSAGENUMBER = "number";
    public static final String COLUMN_CHAT_RECEIVER = "receiver";
    public static final String COLUMN_CHAT_TYPE = "type";

    private static final String DATABASE_NAME = "chats.db";
    private static final int DATABASE_VERSION = 13;

    // Database creation sql statement
    private static final String DATABASE_CREATE_TABLE_CHAT = "create table "
            + TABLE_CHAT + "("
            + COLUMN_CHAT_ID
            + " integer primary key autoincrement, "
            + COLUMN_CHAT_NAME
            + " text not null,"
            + COLUMN_CHAT_MESSAGENUMBER
            + " text not null,"
            + COLUMN_CHAT_RECEIVER
            + " text not null,"
            + COLUMN_CHAT_TYPE
            + " integer"
            + ");";
    private static final String DATABASE_CREATE_TABLE_MESSAGE_COLUMS =
            "("+
             COLUMN_MESSAGES_ID
            + " integer primary key autoincrement, "
            + COLUMN_MESSAGES_MSG
            + " text not null,"
            + COLUMN_MESSAGES_SENDER
            + " INTEGER, "
            + COLUMN_MESSAGES_TIME
            + " text not null, "
            + COLUMN_MESSAGES_READ
            + " integer,"
            + COLUMN_MESSAGES_TYPE
            + " integer"
            +");";
    private final Context context;

    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TABLE_CHAT);
    }
    public void newMessageDatabase(int messageDatabaseID){

        SQLiteDatabase db = context.getApplicationContext().openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
        db.execSQL("create table messagesforchat" + messageDatabaseID + " " + DATABASE_CREATE_TABLE_MESSAGE_COLUMS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        List<String> tables = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String tableName = cursor.getString(1);
            if (!tableName.equals("android_metadata") &&
                    !tableName.equals("sqlite_sequence"))
                tables.add(tableName);
            cursor.moveToNext();
        }
        cursor.close();

        for(String tableName:tables) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
        db.execSQL(DATABASE_CREATE_TABLE_CHAT);
    }

}