package com.storage.database.core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.modules.fms.File_;
import com.modules.teamtalk.model.TeamTalk_;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.Announcement_;
import com.sagesurfer.models.Event_;
import com.sagesurfer.models.Task_;
import com.sagesurfer.parser.Chat_;
import com.storage.database.constants.TableList_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public final class MainDatabase extends SQLiteOpenHelper {
    private static final String TAG = MainDatabase.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ccc_main";
    private final Context context;
    private SQLiteDatabase database;

    public MainDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Preferences.initialize(context);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_.CREATE_TABLE_UPDATE_LOG);
        db.execSQL(Create_.CREATE_TABLE_TASK_LIST);
        db.execSQL(Create_.CREATE_TABLE_ANNOUNCEMENT);
        db.execSQL(Create_.CREATE_TABLE_MESSAGES);
        db.execSQL(Create_.CREATE_TABLE_FRIENDS);
        db.execSQL(Create_.CREATE_TABLE_ROOM);
        db.execSQL(Create_.CREATE_TABLE_ROOM_MESSAGE);
        db.execSQL(Create_.CREATE_TABLE_CHAT_SETTINGS);
        db.execSQL(Create_.CREATE_TABLE_EVENTS);
        db.execSQL(Create_.CREATE_TABLE_FMS);
        db.execSQL(Create_.CREATE_TABLE_TEAM_TALK);
        db.execSQL(Create_.CREATE_TABLE_COMETCHAT_TRACK);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void open() {
        database = this.getReadableDatabase();
    }

    public void close() {
        if (database != null && database.isOpen())
            database.close();
    }

    /**** Insert Method ****/

    public void insertUpdateLog(String columnName, String utc) {
        open();

        String getCount = "SELECT COUNT(" + General.USER_ID + ") FROM " + TableList_.TABLE_UPDATE_LOG + " WHERE " + General.USER_ID + "=" + Preferences.get(General.USER_ID);
        Cursor cursor = database.rawQuery(getCount, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        String sql;
        if (count == 0) {
            sql = Insert_.insertUpdateLog(columnName, utc, Preferences.get(General.USER_ID));
        } else {
            sql = Update_.updateLog(columnName, utc, Preferences.get(General.USER_ID));
        }
        cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        cursor.close();
    }

    // insert announcement to local database
    public void insertAnnouncement(Announcement_ announcement_) {
        open();
        Cursor cursor = database.rawQuery(Retrieve_.countWhere(TableList_.TABLE_ANNOUNCEMENT, General.ID + "=" + announcement_.getId()), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count == 0) {
            database.insert(TableList_.TABLE_ANNOUNCEMENT, null, SetValue.announcement(announcement_));
        } else {
            database.update(TableList_.TABLE_ANNOUNCEMENT, SetValue.announcement(announcement_), General.ID + "=" + announcement_.getId(), null);
        }
        cursor.close();
    }

    // insert com.sagesurfer.task to local database
    public void insertTask(Task_ task_) {
        open();
        Cursor cursor = database.rawQuery(Retrieve_.countWhere(TableList_.TABLE_TASK_LIST, General.ID + "=" + task_.getId()), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count == 0) {
            database.insert(TableList_.TABLE_TASK_LIST, null, SetValue.task(task_));
        } else {
            database.update(TableList_.TABLE_TASK_LIST, SetValue.task(task_), General.ID + "=" + task_.getId(), null);
        }
        cursor.close();
    }

    /*** Retrieve Method ****/

    public String retrieveUpdateLog(String columnName) {
        open();
        String row_count = "SELECT COUNT(" + General.USER_ID + ") FROM " + TableList_.TABLE_UPDATE_LOG;
        Cursor cursor = database.rawQuery(row_count, null);
        cursor.moveToFirst();
        int total_count = cursor.getInt(0);
        cursor.close();
        if (total_count > 1) {
            String delete_record = "DELETE FROM " + TableList_.TABLE_UPDATE_LOG + " WHERE " + General.USER_ID + "!=" + Preferences.get(General.USER_ID);
            cursor = database.rawQuery(delete_record, null);
            cursor.moveToFirst();
            cursor.close();
        }

        String utc = "0";
        String getCount = "SELECT  COUNT(" + General.USER_ID + ") FROM " + TableList_.TABLE_UPDATE_LOG + " WHERE " + General.USER_ID + "=" + Preferences.get(General.USER_ID);

        cursor = database.rawQuery(getCount, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            if (count > 0) {
                String sql = Retrieve_.getUpdateLog(columnName, Preferences.get(General.USER_ID));
                cursor = database.rawQuery(sql, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    utc = cursor.getString(0);
                    if (utc == null || utc.trim().length() <= 0 || utc.equalsIgnoreCase("null")) {
                        utc = "0";
                    }
                }
            }
        }
        return utc;
    }

    // fetch all announcement records from local database
    public ArrayList<Announcement_> retrieveAnnouncement() {
        open();
        ArrayList<Announcement_> announcementArrayList = new ArrayList<>();
        String getSql = "SELECT * FROM " + TableList_.TABLE_ANNOUNCEMENT;
        Cursor cursor = database.rawQuery(getSql, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Announcement_ announcement_ = new Announcement_();
                    announcement_.setStatus(1);
                    announcement_.setId(cursor.getInt(cursor.getColumnIndex(General.ID)));
                    announcement_.setDescription(cursor.getString(cursor.getColumnIndex(General.DESCRIPTION)));
                    announcement_.setDate(cursor.getInt(cursor.getColumnIndex(General.LAST_UPDATED)));
                    announcement_.setUsername(cursor.getString(cursor.getColumnIndex(General.USERNAME)));
                    announcement_.setCreatedBy(cursor.getString(cursor.getColumnIndex(General.CREATED_BY)));
                    announcement_.setImage(cursor.getString(cursor.getColumnIndex(General.IMAGE)));
                    announcement_.setTeamName(cursor.getString(cursor.getColumnIndex(General.TEAM_NAME)));
                    announcement_.setIsRead(cursor.getInt(cursor.getColumnIndex(General.IS_READ)));
                    announcement_.setTeam_id(cursor.getInt(cursor.getColumnIndex(General.TEAM_ID)));
                    announcement_.setIsDelete(cursor.getInt(cursor.getColumnIndex(General.IS_DELETE)));

                    announcementArrayList.add(announcement_);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        Collections.reverse(announcementArrayList);
        return announcementArrayList;
    }

    // delete entry based on id from given database table and column
    public void deleteEntry(String table_name, String id, String column) {
        open();
        String getSql = Retrieve_.countWhere(table_name, General.ID + "='" + id + "'");
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(getSql, null);
            if (database != null && database.isOpen()){
                cursor.moveToFirst();
                int count = cursor.getInt(0);
                if (count > 0) {
                    String deleteSql = "DELETE FROM " + table_name + " WHERE " + column + "= '" + id + "'";
                    cursor = database.rawQuery(deleteSql, null);
                    cursor.moveToFirst();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // fetch  com.sagesurfer.task from local database
    public ArrayList<Task_> retrieveTask(int own_or_team) {
        //team=1;own=0
        open();
        ArrayList<Task_> taskArrayList = new ArrayList<>();
        String getSql = "SELECT * FROM " + TableList_.TABLE_TASK_LIST + " WHERE " + General.OWN_OR_TEAM + "=" + own_or_team;
        Cursor cursor = database.rawQuery(getSql, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Task_ task_ = new Task_();
                    task_.setStatus(1);
                    task_.setId(cursor.getInt(cursor.getColumnIndex(General.ID)));
                    task_.setDate(cursor.getInt(cursor.getColumnIndex(General.DUE_DATE)));
                    task_.setTitle(cursor.getString(cursor.getColumnIndex(General.TITLE)));
                    task_.setDescription(cursor.getString(cursor.getColumnIndex(General.DESCRIPTION)));
                    task_.setAddedBy(cursor.getString(cursor.getColumnIndex(General.ADDED_BY)));
                    task_.setTeamName(cursor.getString(cursor.getColumnIndex(General.TEAM_NAME)));
                    task_.setToDoStatus(cursor.getString(cursor.getColumnIndex(General.TO_DO_STATUS)));
                    task_.setFullName(cursor.getString(cursor.getColumnIndex(General.FULL_NAME)));
                    task_.setPriority(cursor.getString(cursor.getColumnIndex(General.PRIORITY)));
                    task_.setImage(cursor.getString(cursor.getColumnIndex(General.IMAGE)));
                    task_.setTeamId(cursor.getInt(cursor.getColumnIndex(General.TEAM_ID)));
                    task_.setIsRead(cursor.getInt(cursor.getColumnIndex(General.IS_READ)));
                    task_.setIsDelete(cursor.getInt(cursor.getColumnIndex(General.IS_DELETE)));
                    task_.setIsOwner(cursor.getInt(cursor.getColumnIndex(General.IS_OWNER)));
                    task_.setOwnOrTeam(cursor.getInt(cursor.getColumnIndex(General.OWN_OR_TEAM)));

                    taskArrayList.add(task_);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        Collections.reverse(taskArrayList);
        return taskArrayList;
    }

    // update read record for given column name and table
    public void updateRead(String columnName, String tableName, String value, String id) {
        open();
        String update = Update_.updateRead(columnName, tableName, value, id);
        Cursor cursor = database.rawQuery(update, null);
        cursor.moveToFirst();
        cursor.close();
    }

    // insert message record to local database
    public long insertMessage(String id, String to, String from, String self, String is_read,
                              String message, String name, String sent, String message_type,
                              String download) {
        open();

        String getCount = "SELECT COUNT(" + General.ID + ") FROM " + TableList_.TABLE_MESSAGES + " WHERE " + General.ID + "=" + id;
        Cursor cursor = database.rawQuery(getCount, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count <= 0) {
            return database.insert(TableList_.TABLE_MESSAGES, null, SetValue.message(id, to, from, self, is_read, message, name, sent, message_type, download));
        }
        cursor.close();
        return 0;
    }

    // add friends to local database record
    public void insertFriend(String name, String status_message, String image, String id, String status, String last_seen, String channel) {
        open();
        Cursor cursor = database.rawQuery(Retrieve_.countWhere(TableList_.TABLE_FRIENDS, General.ID + "=" + id), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count == 0) {
            database.insert(TableList_.TABLE_FRIENDS, null, SetValue.friend(name, status_message, image, id, status, last_seen, channel, "0"));
        } else {
            database.update(TableList_.TABLE_FRIENDS, SetValue.friend(name, status_message, image, id, status, last_seen, channel, "0"), General.ID + "=" + id, null);
        }
        cursor.close();
    }

    // insert chat room message
    public long insertRoomMessage(String id, String room_id, String from, String message,
                                  String name, String sent, String message_type,
                                  String text_color, String download, String self) {
        open();

        String getCount = "SELECT COUNT(" + General.ID + ") FROM " + TableList_.TABLE_ROOM_MESSAGE + " WHERE " + General.ID + "=" + id;
        Cursor cursor = database.rawQuery(getCount, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        long result;
        if (count <= 0) {
            result = database.insert(TableList_.TABLE_ROOM_MESSAGE, null, SetValue.roomMessage(id, room_id, from, message, name, sent, message_type, text_color, download, self));
        } else {
            result = database.update(TableList_.TABLE_ROOM_MESSAGE, SetValue.roomMessage(id, room_id, from, message, name, sent, message_type, text_color, download, self), General.ID + "=" + id, null);
        }
        cursor.close();
        return result;
    }

    public void insertSetting(String id, String name, String status_message, String status) {
        open();
        Cursor cursor = database.rawQuery(Retrieve_.countWhere(TableList_.TABLE_CHAT_SETTINGS, General.ID + "=" + id), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count > 1) {
            flushTables(TableList_.TABLE_CHAT_SETTINGS);
            database.insert(TableList_.TABLE_CHAT_SETTINGS, null, SetValue.settings(id, name, status_message, status));
        } else if (count == 0) {
            database.insert(TableList_.TABLE_CHAT_SETTINGS, null, SetValue.settings(id, name, status_message, status));
        } else {
            database.update(TableList_.TABLE_CHAT_SETTINGS, SetValue.settings(id, name, status_message, status), General.ID + "=" + id, null);
        }
        cursor.close();
    }

    private void flushTables(String table_name) {
        open();
        String deleteQuery = "DELETE FROM " + table_name;
        Cursor cursor = database.rawQuery(deleteQuery, null);
        cursor.moveToFirst();
        cursor.close();
    }

    public String getFriendDetails(String id) {
        String name = "Unknown";
        open();
        Cursor cursor = database.rawQuery(Retrieve_.countWhere(TableList_.TABLE_FRIENDS, General.ID + "=" + id), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count > 0) {
            cursor = database.rawQuery(Retrieve_.getFriend(id), null);
            if (cursor != null) {
                cursor.moveToFirst();
                name = cursor.getString(0);
            }
        }
        assert cursor != null;
        cursor.close();
        return name;
    }

    // fetch unread message count from local database
    public int getUnreadCount(String id) {
        int count_unread = 0;
        open();
        Cursor cursor = database.rawQuery(Retrieve_.countWhere(TableList_.TABLE_FRIENDS, General.ID + "=" + id), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count > 0) {
            cursor = database.rawQuery(Retrieve_.getUnreadCount(id), null);
            if (cursor != null) {
                cursor.moveToFirst();
                count_unread = cursor.getInt(0);
            }
        }
        assert cursor != null;
        cursor.close();
        return count_unread;
    }

    // fetch friend list from local database
    public ArrayList<HashMap<String, String>> getFriends() {
        open();
        String sql = "SELECT * FROM " + TableList_.TABLE_FRIENDS;
        Cursor cursor = database.rawQuery(sql, null);
        ArrayList<HashMap<String, String>> friendsList = Chat_.friendsFromDb(cursor, context);
        cursor.moveToFirst();
        cursor.close();
        return friendsList;
    }

    // update friend/user record based on user ids
    public void updateFriend(String user_id, String tableName, String columnName, String value) {
        open();
        Cursor cursor = database.rawQuery(Retrieve_.countWhere(tableName, General.ID + "=" + user_id), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count > 0) {
            String updateQuery = Update_.updateFriend(user_id, tableName, columnName, value);
            cursor = database.rawQuery(updateQuery, null);
            cursor.moveToFirst();
            cursor.close();
        }
    }

    // fetch message list from local database
    public ArrayList<HashMap<String, String>> getMessages(String user_id, String friend_id) {
        open();
        Cursor mCursor = database.rawQuery(Retrieve_.getMessages(user_id, friend_id), null);
        ArrayList<HashMap<String, String>> messageList = Chat_.dbMyMessages(mCursor);
        mCursor.close();
        return messageList;
    }

    public void updateMessage(String id, String message, String download) {
        open();
        String updateQuery = Update_.updateMessage(id, message, download);
        Cursor cursor = database.rawQuery(updateQuery, null);
        cursor.moveToFirst();
        cursor.close();
    }

    // update message read/unread once user see it
    public void updateMessageRead(String id) {
        open();
        String updateQuery = Update_.updateMessageRead(id);
        Cursor cursor = database.rawQuery(updateQuery, null);
        cursor.moveToFirst();
        cursor.close();
    }

    // insert events to local database
    public void insertEvent(Event_ event_) {
        open();
        Cursor cursor = database.rawQuery(Retrieve_.countWhere(TableList_.TABLE_EVENTS, General.ID + "=" + event_.getId()), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count == 0) {
            database.insert(TableList_.TABLE_EVENTS, null, SetValue.event(event_));
        } else {
            database.update(TableList_.TABLE_EVENTS, SetValue.event(event_), General.ID + "=" + event_.getId(), null);
        }
        cursor.close();
    }

    // insert fms to local database
    public void insertFMS(File_ file_) {
        open();
        Cursor cursor = null;
        cursor = database.rawQuery(Retrieve_.countWhere(TableList_.TABLE_FMS, General.ID + "=" + file_.getId()), null);

        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count == 0) {
            database.insert(TableList_.TABLE_FMS, null, SetValue.fms(file_));
        } else {
            database.update(TableList_.TABLE_FMS, SetValue.fms(file_), General.ID + "=" + file_.getId(), null);
        }
        cursor.close();
    }

    // insert team talk to local database
    public void insertTeamTalk(TeamTalk_ teamTalk_) {
        open();
        Cursor cursor = database.rawQuery(Retrieve_.countWhere(TableList_.TABLE_TEAM_TALK, General.ID + "=" + teamTalk_.getId()), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count == 0) {
            database.insert(TableList_.TABLE_TEAM_TALK, null, SetValue.teamTalk(teamTalk_));
        } else {
            database.update(TableList_.TABLE_TEAM_TALK, SetValue.teamTalk(teamTalk_), General.ID + "=" + teamTalk_.getId(), null);
        }
        cursor.close();
    }

    // fetch all announcement records from local database
    public ArrayList<Event_> retrieveEvents() {
        open();
        ArrayList<Event_> eventsArrayList = new ArrayList<>();
        String getSql = "SELECT * FROM " + TableList_.TABLE_EVENTS;
        Cursor cursor = database.rawQuery(getSql, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Event_ event_ = new Event_();
                    event_.setStatus(1);
                    event_.setId(cursor.getInt(cursor.getColumnIndex(General.ID)));
                    event_.setTimestamp(cursor.getInt(cursor.getColumnIndex(General.TIMESTAMP)));
                    event_.setEvent_time(cursor.getString(cursor.getColumnIndex(General.EVENT_TIME)));
                    event_.setName(cursor.getString(cursor.getColumnIndex(General.NAME)));
                    event_.setDescription(cursor.getString(cursor.getColumnIndex(General.DESCRIPTION)));
                    event_.setUsername(cursor.getString(cursor.getColumnIndex(General.USERNAME)));
                    event_.setImage(cursor.getString(cursor.getColumnIndex(General.IMAGE)));
                    event_.setTeam_id(cursor.getInt(cursor.getColumnIndex(General.TEAM_ID)));
                    event_.setTeam_name(cursor.getString(cursor.getColumnIndex(General.TEAM_NAME)));
                    event_.setDate_string(cursor.getString(cursor.getColumnIndex(General.DATE_STRING)));
                    event_.setParticipants(cursor.getInt(cursor.getColumnIndex(General.PARTICIPANTS)));
                    event_.setIs_read(cursor.getInt(cursor.getColumnIndex(General.IS_READ)));
                    event_.setLocation(cursor.getString(cursor.getColumnIndex(General.LOCATION)));
                    event_.setIs_delete(cursor.getInt(cursor.getColumnIndex(General.IS_DELETE)));

                    eventsArrayList.add(event_);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        Collections.reverse(eventsArrayList);
        return eventsArrayList;
    }

    // fetch all announcement records from local database
    public ArrayList<File_> retrieveFMS() {
        open();
        ArrayList<File_> fileArrayList = new ArrayList<>();
        String getSql = "SELECT * FROM " + TableList_.TABLE_FMS;
        Cursor cursor = database.rawQuery(getSql, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    File_ file_ = new File_();
                    file_.setStatus(1);
                    file_.setId(cursor.getInt(cursor.getColumnIndex(General.ID)));
                    file_.setGroupId(cursor.getInt(cursor.getColumnIndex(General.GROUP_ID)));
                    file_.setUserId(cursor.getInt(cursor.getColumnIndex(General.USER_ID)));
                    file_.setIsRead(cursor.getInt(cursor.getColumnIndex(General.IS_READ)));
                    file_.setIsDefault(cursor.getInt(cursor.getColumnIndex(General.IS_DEFAULT)));
                    file_.setCheckIn(cursor.getInt(cursor.getColumnIndex(General.CHECK_IN)));
                    file_.setPermission(cursor.getInt(cursor.getColumnIndex(General.PERMISSION)));
                    file_.setDate(cursor.getInt(cursor.getColumnIndex(General.POSTED_DATE)));
                    file_.setTeamName(cursor.getString(cursor.getColumnIndex(General.TEAM_NAME)));
                    file_.setUserName(cursor.getString(cursor.getColumnIndex(General.USERNAME)));
                    file_.setFullName(cursor.getString(cursor.getColumnIndex(General.FULL_NAME)));
                    file_.setRealName(cursor.getString(cursor.getColumnIndex(General.REAL_NAME)));
                    file_.setComment(cursor.getString(cursor.getColumnIndex(General.COMMENT)));
                    file_.setDescription(cursor.getString(cursor.getColumnIndex(General.DESCRIPTION)));
                    file_.setSize(cursor.getInt(cursor.getColumnIndex(General.SIZE)));
                    file_.setIs_delete(cursor.getInt(cursor.getColumnIndex(General.IS_DELETE)));

                    fileArrayList.add(file_);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        Collections.reverse(fileArrayList);
        return fileArrayList;
    }

    // fetch all announcement records from local database
    public ArrayList<TeamTalk_> retrieveTeamTalk() {
        open();
        ArrayList<TeamTalk_> teamTalkArrayList = new ArrayList<>();
        String getSql = "SELECT * FROM " + TableList_.TABLE_TEAM_TALK;
        Cursor cursor = database.rawQuery(getSql, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    TeamTalk_ teamTalk_ = new TeamTalk_();
                    teamTalk_.setStatus(1);
                    teamTalk_.setId(cursor.getInt(cursor.getColumnIndex(General.ID)));
                    teamTalk_.setTitle(cursor.getString(cursor.getColumnIndex(General.TITLE)));
                    teamTalk_.setMessage(cursor.getString(cursor.getColumnIndex(General.MESSAGE)));
                    teamTalk_.setTeamId(cursor.getInt(cursor.getColumnIndex(General.TEAM_ID)));
                    teamTalk_.setTeamName(cursor.getString(cursor.getColumnIndex(General.TEAM_NAME)));
                    teamTalk_.setAddedBy(cursor.getString(cursor.getColumnIndex(General.ADDED_BY)));
                    teamTalk_.setFullName(cursor.getString(cursor.getColumnIndex(General.FULL_NAME)));
                    teamTalk_.setImage(cursor.getString(cursor.getColumnIndex(General.IMAGE)));
                    teamTalk_.setDate(cursor.getInt(cursor.getColumnIndex(General.LAST_UPDATED)));
                    teamTalk_.setIsRead(cursor.getInt(cursor.getColumnIndex(General.IS_READ)));
                    teamTalk_.setCount(cursor.getInt(cursor.getColumnIndex(General.COUNT)));
                    teamTalk_.setIsDelete(cursor.getInt(cursor.getColumnIndex(General.IS_DELETE)));

                    teamTalkArrayList.add(teamTalk_);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        Collections.reverse(teamTalkArrayList);
        return teamTalkArrayList;
    }
}
