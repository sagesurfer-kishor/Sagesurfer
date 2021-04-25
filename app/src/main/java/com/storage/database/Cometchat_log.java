package com.storage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Cometchat_log extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "CometChat";
    public static final String TABLE_NAME = "chat_detail_tracking";

    public static final String CHAT_ID = "chat_id";
    public static final String FROM_CHAT = "from_chat";
    public static final String TO_CHAT = "to_chat";
    public static final String COMET_CHAT_TYPE = "comet_chat_type";
    public static final String CHAT_MESSAGE_ID = "chat_message_id";
    public static final String CHAT_GROUP_ID = "chat_group_id";
    public static final String PRIVATE_GROUP_ID = "private_group_id";
    public static final String CHAT_TYPE = "chat_type";
    public static final String READ_CHAT = "read_chat";
    public static final String CUSTOM_DATE = "custom_date";
    public static final String DURATION = "duration";
    public static final String IS_DURATION_ADDED = "is_duration_added";
    public static final String DEVICE_TRACKING = "device_tracking";
    public static final String DATE_TIME = "date_time";
    public static final String MODIFIED_DATE = "modified_date";

    public static final String COLUMN_STATUS = "status";

    //database version
    private static final int DB_VERSION = 1;

    //Constructor
    public Cometchat_log(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + CHAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FROM_CHAT + " VARCHAR(500), " + TO_CHAT + " VARCHAR(500), " + COMET_CHAT_TYPE + " INTEGER(150), " + CHAT_MESSAGE_ID + " VARCHAR(500), " + CHAT_GROUP_ID + " VARCHAR(500), " + PRIVATE_GROUP_ID + " VARCHAR(500), " + CHAT_TYPE + " TINYINT, " + READ_CHAT +
                " TINYINT," + CUSTOM_DATE + " VARCHAR(500)," + DURATION + " VARCHAR(500)," + IS_DURATION_ADDED + " VARCHAR(500)," + DEVICE_TRACKING + " VARCHAR(500)," + DATE_TIME + " VARCHAR(500)," + MODIFIED_DATE + " VARCHAR(500))";
        db.execSQL(sql);
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS Persons";
        db.execSQL(sql);
        onCreate(db);
    }

    /*
     * This method is taking two arguments
     * first one is the name that is to be saved
     * second one is the status
     * 0 means the name is synced with the server
     * 1 means the name is not synced with the server
     * */

    public boolean addName(String from_chat, String to_chat, String comet_chat_type, String chat_message_id, String chat_group_id, String private_group_id, String chat_type, String read_chat
            , String custom_date, String duration, String is_duration_added, String device_tracking, String date_time, String modified_date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(FROM_CHAT, from_chat);
        contentValues.put(TO_CHAT, to_chat);
        contentValues.put(COMET_CHAT_TYPE, comet_chat_type);
        contentValues.put(CHAT_MESSAGE_ID, chat_message_id);
        contentValues.put(CHAT_GROUP_ID, chat_group_id);
        contentValues.put(PRIVATE_GROUP_ID, private_group_id);
        contentValues.put(CHAT_TYPE, chat_type);
        contentValues.put(READ_CHAT, read_chat);
        contentValues.put(CUSTOM_DATE, custom_date);
        contentValues.put(DURATION, duration);
        contentValues.put(IS_DURATION_ADDED, is_duration_added);
        contentValues.put(DEVICE_TRACKING, device_tracking);
        contentValues.put(DATE_TIME, date_time);
        contentValues.put(MODIFIED_DATE, modified_date);

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    /*
     * This method taking two arguments
     * first one is the id of the name for which
     * we have to update the sync status
     * and the second one is the status that will be changed
     * */
    public boolean updateNameStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, contentValues, CHAT_ID + "=" + id, null);
        db.close();
        return true;
    }

    /*
     * this method will give us all the name stored in sqlite
     * */

    public Cursor getNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + CHAT_ID + " DESC LIMIT 10;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


}
