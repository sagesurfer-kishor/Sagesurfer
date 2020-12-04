package com.storage.database.core;

import android.content.ContentValues;

import com.modules.fms.File_;
import com.modules.teamtalk.model.TeamTalk_;
import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.Announcement_;
import com.sagesurfer.models.Event_;
import com.sagesurfer.models.Task_;

/**
 * @author Kailash Karankal
 */

/*
 * Set values using ContentValues with key value pairs
 */

final class SetValue {

    static ContentValues announcement(Announcement_ announcement_) {

        ContentValues announcementValue = new ContentValues();
        announcementValue.put(General.ID, announcement_.getId());
        announcementValue.put(General.DESCRIPTION, announcement_.getDescription());
        announcementValue.put(General.LAST_UPDATED, announcement_.getDate());
        announcementValue.put(General.USERNAME, announcement_.getUsername());
        announcementValue.put(General.CREATED_BY, announcement_.getCreatedBy());
        announcementValue.put(General.TEAM_NAME, announcement_.getTeamName());
        announcementValue.put(General.IS_READ, announcement_.getIsRead());
        announcementValue.put(General.TEAM_ID, announcement_.getTeamId());
        announcementValue.put(General.IS_DELETE, announcement_.getIsDelete());
        announcementValue.put(General.IMAGE, announcement_.getImage());
        return announcementValue;
    }

    static ContentValues task(Task_ task_) {

        ContentValues taskValue = new ContentValues();
        taskValue.put(General.ID, task_.getId());
        taskValue.put(General.DUE_DATE, task_.getDate());
        taskValue.put(General.TITLE, task_.getTitle());
        taskValue.put(General.DESCRIPTION, task_.getDescription());
        taskValue.put(General.ADDED_BY, task_.getAddedBy());
        taskValue.put(General.TEAM_NAME, task_.getTeamName());
        taskValue.put(General.TO_DO_STATUS, task_.getToDoStatus());
        taskValue.put(General.FULL_NAME, task_.getFullName());
        taskValue.put(General.PRIORITY, task_.getPriority());
        taskValue.put(General.IMAGE, task_.getImage());
        taskValue.put(General.TEAM_ID, task_.getTeamId());
        taskValue.put(General.IS_READ, task_.getIsRead());
        taskValue.put(General.IS_DELETE, task_.getIsDelete());
        taskValue.put(General.IS_OWNER, task_.getIsOwner());
        taskValue.put(General.OWN_OR_TEAM, task_.getOwnOrTeam());
        return taskValue;
    }

    public static ContentValues message(String id, String to, String from, String self, String is_read,
                                        String message, String name, String sent, String message_type,
                                        String download) {

        ContentValues values = new ContentValues();
        values.put(General.ID, id);
        values.put(Chat.NAME, name);
        values.put(General.IS_READ, is_read);
        values.put(Chat.TO + "_" + General.ID, to);
        values.put(Chat.FROM + "_" + General.ID, from);
        values.put(Chat.MESSAGE, message);
        values.put(Chat.SENT, sent);
        values.put(Chat.SELF, self);
        values.put(General.DOWNLOAD_STATUS, download);
        values.put(Chat.MESSAGE_TYPE, message_type);

        return values;
    }

    static ContentValues friend(String name, String status_message,
                                String image, String id, String status,
                                String last_seen, String channel, String typing) {

        ContentValues profileValues = new ContentValues();
        profileValues.put(General.ID, id);
        profileValues.put(Chat.NAME, name);
        profileValues.put(Chat.MESSAGE, status_message);
        profileValues.put(Chat.PHOTO, image);
        profileValues.put(Chat.STATUS, status);
        profileValues.put(Chat.LAST_SEEN, last_seen);
        profileValues.put(Chat.CHANNEL, channel);
        profileValues.put(Chat.TYPING, typing);
        return profileValues;
    }

    static ContentValues roomMessage(String id, String room_id, String from, String message,
                                     String name, String sent, String message_type,
                                     String text_color, String download, String self) {

        ContentValues messageValues = new ContentValues();
        messageValues.put(General.ID, id);
        messageValues.put(Chat.ROOM_ID, room_id);
        messageValues.put(Chat.FROM + "_" + General.ID, from);
        messageValues.put(Chat.MESSAGE, message);
        messageValues.put(General.NAME, name);
        messageValues.put(Chat.SENT, sent);
        messageValues.put(Chat.MESSAGE_TYPE, message_type);
        messageValues.put(Chat.TEXT_COLOR, text_color);
        messageValues.put(Chat.SELF, self);
        messageValues.put(General.DOWNLOAD_STATUS, download);

        return messageValues;
    }

    public static ContentValues settings(String id, String name, String status_message, String status) {

        ContentValues profileValues = new ContentValues();
        profileValues.put(General.ID, id);
        profileValues.put(Chat.NAME, name);
        profileValues.put(Chat.MESSAGE, status_message);
        profileValues.put(Chat.STATUS, status);

        return profileValues;
    }

    static ContentValues event(Event_ event_) {

        ContentValues eventValue = new ContentValues();
        eventValue.put(General.ID, event_.getId());
        eventValue.put(General.TIMESTAMP, event_.getTimestamp());
        eventValue.put(General.EVENT_TIME, event_.getEvent_time());
        eventValue.put(General.NAME, event_.getName());
        eventValue.put(General.DESCRIPTION, event_.getDescription());
        eventValue.put(General.USERNAME, event_.getUsername());
        eventValue.put(General.IMAGE, event_.getImage());
        eventValue.put(General.TEAM_ID, event_.getTeam_id());
        eventValue.put(General.TEAM_NAME, event_.getTeam_name());
        eventValue.put(General.DATE_STRING, event_.getDate_string());
        eventValue.put(General.PARTICIPANTS, event_.getParticipants());
        eventValue.put(General.IS_READ, event_.getIs_read());
        eventValue.put(General.LOCATION, event_.getLocation());
        eventValue.put(General.IS_DELETE, event_.getIs_delete());
        return eventValue;
    }

    static ContentValues fms(File_ announcement_) {

        ContentValues fileValue = new ContentValues();
        fileValue.put(General.ID, announcement_.getId());
        fileValue.put(General.GROUP_ID, announcement_.getGroupId());
        fileValue.put(General.USER_ID, announcement_.getUserId());
        fileValue.put(General.IS_READ, announcement_.getIsRead());
        fileValue.put(General.IS_DEFAULT, announcement_.getIsDefault());
        fileValue.put(General.CHECK_IN, announcement_.getCheckIn());
        fileValue.put(General.PERMISSION, announcement_.getPermission());
        fileValue.put(General.POSTED_DATE, announcement_.getDate());
        fileValue.put(General.TEAM_NAME, announcement_.getTeamName());
        fileValue.put(General.USERNAME, announcement_.getUserName());
        fileValue.put(General.FULL_NAME, announcement_.getFullName());
        fileValue.put(General.REAL_NAME, announcement_.getRealName());
        fileValue.put(General.COMMENT, announcement_.getComment());
        fileValue.put(General.DESCRIPTION, announcement_.getDescription());
        fileValue.put(General.SIZE, announcement_.getSize());
        fileValue.put(General.IS_DELETE, announcement_.getIs_delete());
        return fileValue;
    }

    static ContentValues teamTalk(TeamTalk_ teamTalk_) {

        ContentValues teamTalkValue = new ContentValues();
        teamTalkValue.put(General.ID, teamTalk_.getId());
        teamTalkValue.put(General.TITLE, teamTalk_.getTitle());
        teamTalkValue.put(General.MESSAGE, teamTalk_.getMessage());
        teamTalkValue.put(General.TEAM_ID, teamTalk_.getTeamId());
        teamTalkValue.put(General.TEAM_NAME, teamTalk_.getTeamName());
        teamTalkValue.put(General.ADDED_BY, teamTalk_.getAddedby());
        teamTalkValue.put(General.FULL_NAME, teamTalk_.getFullName());
        teamTalkValue.put(General.IMAGE, teamTalk_.getImage());
        teamTalkValue.put(General.LAST_UPDATED, teamTalk_.getDate());
        teamTalkValue.put(General.IS_READ, teamTalk_.getIsRead());
        teamTalkValue.put(General.COUNT, teamTalk_.getCount());
        teamTalkValue.put(General.IS_DELETE, teamTalk_.getIsDelete());
        return teamTalkValue;
    }
}
