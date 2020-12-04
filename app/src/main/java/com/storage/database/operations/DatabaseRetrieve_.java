package com.storage.database.operations;

import android.content.Context;

import com.sagesurfer.models.Announcement_;
import com.sagesurfer.models.Task_;
import com.storage.database.core.MainDatabase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Kailash Karankal
 */

public final class DatabaseRetrieve_ {
    private final MainDatabase mainDatabase;

    public DatabaseRetrieve_(Context _context) {
        mainDatabase = new MainDatabase(_context);
    }

    public final String retrieveUpdateLog(String columnName) {
        String utc = mainDatabase.retrieveUpdateLog(columnName);
        mainDatabase.close();
        return utc;
    }

    public final ArrayList<Announcement_> retrieveAnnouncement() {
        ArrayList<Announcement_> announcementArrayList = mainDatabase.retrieveAnnouncement();
        mainDatabase.close();
        return announcementArrayList;
    }

    public final ArrayList<Task_> retrieveTask(int own_or_team) {
        ArrayList<Task_> taskArrayList = mainDatabase.retrieveTask(own_or_team);
        mainDatabase.close();
        return taskArrayList;
    }

    public String getFriendDetails(String id) {
        String name = mainDatabase.getFriendDetails(id);
        mainDatabase.close();
        return name;
    }

    public int getUnreadCount(String id) {
        int count_unread = mainDatabase.getUnreadCount(id);
        mainDatabase.close();
        return count_unread;
    }

    public ArrayList<HashMap<String, String>> getFriends() {
        ArrayList<HashMap<String, String>> friendsList;
        friendsList = mainDatabase.getFriends();
        mainDatabase.close();
        return friendsList;
    }

    public ArrayList<HashMap<String, String>> getMessages(String user_id, String friend_id) {
        ArrayList<HashMap<String, String>> messageList = mainDatabase.getMessages(user_id, friend_id);
        mainDatabase.close();
        return messageList;
    }
}
