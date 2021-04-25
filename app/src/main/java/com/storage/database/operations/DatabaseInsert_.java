package com.storage.database.operations;

import android.content.Context;

import com.modules.fms.File_;
import com.modules.teamtalk.model.TeamTalk_;
import com.sagesurfer.models.Announcement_;
import com.sagesurfer.models.Event_;
import com.sagesurfer.models.Task_;
import com.storage.database.core.MainDatabase;

/**
 * @author Kailash Karankal
 */

// insert values to table

public final class DatabaseInsert_ {
    private final MainDatabase mainDatabase;

    public DatabaseInsert_(Context _context) {
        mainDatabase = new MainDatabase(_context);
    }

    public final void insertUpdateLog(String columnName, String utc) {
        mainDatabase.insertUpdateLog(columnName, utc);
        mainDatabase.close();
    }

    public final void insertAnnouncement(Announcement_ announcement_) {
        mainDatabase.insertAnnouncement(announcement_);
        mainDatabase.close();
    }

    public final void insertTask(Task_ task_) {
        mainDatabase.insertTask(task_);
        mainDatabase.close();
    }

    public long insertMessage(String id, String to, String from, String self, String is_read,
                              String message, String name, String sent, String message_type, String download) {
        long result = mainDatabase.insertMessage(id, to, from, self, is_read, message, name, sent, message_type, download);
        mainDatabase.close();
        return result;
    }

    public void insertFriend(String name, String status_message, String image, String id, String status, String last_seen, String channel) {
        mainDatabase.insertFriend(name, status_message, image, id, status, last_seen, channel);
        mainDatabase.close();
    }

    public long insertRoomMessage(String id, String room_id, String from, String message,
                                  String name, String sent, String message_type,
                                  String text_color, String download, String self) {
        long result = mainDatabase.insertRoomMessage(id, room_id, from, message, name, sent, message_type, text_color, download, self);
        mainDatabase.close();
        return result;
    }

    public void insertSetting(String id, String name, String status_message, String status) {
        mainDatabase.insertSetting(id, name, status_message, status);
        mainDatabase.close();
    }

    public final void insertEvent(Event_ event_) {
        mainDatabase.insertEvent(event_);
        mainDatabase.close();
    }

    public final void insertFMS(File_ file_) {
        mainDatabase.insertFMS(file_);
        mainDatabase.close();
    }

    public final void insertTeamTalk(TeamTalk_ TeamTalk_) {
        mainDatabase.insertTeamTalk(TeamTalk_);
        mainDatabase.close();
    }
}
