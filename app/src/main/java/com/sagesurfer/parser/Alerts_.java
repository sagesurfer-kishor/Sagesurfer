package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.modules.fms.AllFolder_;
import com.modules.fms.File_;
import com.modules.fms.Folder_;
import com.modules.platform.DashboardMessage_;
import com.modules.teamtalk.model.Comments_;
import com.modules.teamtalk.model.TeamTalk_;
import com.modules.wall.Feed_;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.Announcement_;
import com.sagesurfer.models.Event_;
import com.sagesurfer.models.MessageBoard_;
import com.sagesurfer.models.Task_;
import com.storage.database.operations.DatabaseInsert_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish M (girish@sagesurfer.com)
 * Created on 21-07-2017
 * Last Modified 26-09-2017
 */

public class Alerts_ {

    public static ArrayList<Announcement_> parseAnnouncement(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Announcement_> announcementList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) != 13) {
            JsonObject jsonObject = GetJson_.getJson(response);
            String utc = jsonObject.get("utc").getAsString();

            DatabaseInsert_ databaseInsert_ = new DatabaseInsert_(_context);
            databaseInsert_.insertUpdateLog(General.ANNOUNCEMENT, utc);

            if (Error_.noData(response, jsonName, _context) == 2) {
                return announcementList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Announcement_>>() {
            }.getType();
            announcementList = gson.fromJson(GetJson_.getArray(response, Actions_.ANNOUNCEMENT).toString(), listType);
        }
        return announcementList;
    }

    public static ArrayList<Feed_> parseShareTeamList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Feed_> feedArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) != 13) {
            if (Error_.noData(response, Actions_.GET_SHARE_LIST_TEAM, _context) == 2) {
                Feed_ feed_ = new Feed_(2);
                //feed_.setStatus(2);
                feedArrayList.add(feed_);
                return feedArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Feed_>>() {
            }.getType();
            feedArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.GET_SHARE_LIST_TEAM).toString(), listType);
        }
        return feedArrayList;

    }

    public static ArrayList<Feed_> parseShareFriendList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Feed_> feedArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) != 13) {
            if (Error_.noData(response, Actions_.GET_SHARE_LIST_FRIEND, _context) == 2) {
                Feed_ feed_ = new Feed_(2);
                feedArrayList.add(feed_);
                return feedArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Feed_>>() {
            }.getType();
            feedArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.GET_SHARE_LIST_FRIEND).toString(), listType);
        }
        return feedArrayList;

    }

    public static ArrayList<Task_> parseTask(String response, String jsonName, String utcColumn, Context _context, String TAG) {
        ArrayList<Task_> taskList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) != 13) {
            JsonObject jsonObject = GetJson_.getJson(response);
            String utc = jsonObject.get("utc").getAsString();

            DatabaseInsert_ databaseInsert_ = new DatabaseInsert_(_context);
            databaseInsert_.insertUpdateLog(utcColumn, utc);

            if (Error_.noData(response, jsonName, _context) == 2) {
                return taskList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Task_>>() {
            }.getType();
            taskList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        }
        return taskList;
    }

    public static ArrayList<TeamTalk_> parseTalk(String response, String jsonName, Context _context, String TAG) {
        ArrayList<TeamTalk_> talkList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) != 13) {
            if (Error_.noData(response, jsonName, _context) == 2) {
                TeamTalk_ teamTalk_ = new TeamTalk_();
                teamTalk_.setStatus(2);
                talkList.add(teamTalk_);
                return talkList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<TeamTalk_>>() {
            }.getType();
            talkList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        }
        return talkList;
    }

    public static ArrayList<Comments_> parseTalkComment(String response, Context _context, String TAG) {
        ArrayList<Comments_> commentsList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) != 13) {
            if (Error_.noData(response, Actions_.COMMENTS, _context) == 2) {
                Comments_ comments_ = new Comments_();
                comments_.setStatus(2);
                commentsList.add(comments_);
                return commentsList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Comments_>>() {
            }.getType();
            commentsList = gson.fromJson(GetJson_.getArray(response, Actions_.COMMENTS).toString(), listType);
        }
        return commentsList;
    }

    public static ArrayList<File_> parseFiles(String response, Context _context, String TAG) {
        ArrayList<File_> fileList = new ArrayList<>();
        if (response == null) {
            File_ file_ = new File_();
            file_.setStatus(12);
            fileList.add(file_);
            return fileList;
        }
        if (Error_.oauth(response, _context) == 13) {
            File_ file_ = new File_();
            file_.setStatus(13);
            fileList.add(file_);
            return fileList;
        }
        if (Error_.noData(response, "files", _context) == 2) {
            File_ file_ = new File_();
            file_.setStatus(2);
            fileList.add(file_);
            return fileList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, "files");
        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<File_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, "files").toString(), listType);
            // Logger.error(TAG, fileList.size() + " => jsonArray: " + jsonArray, _context);
        }
        return fileList;
    }

    public static ArrayList<Folder_> parseFolders(String response, Context _context, String TAG) {
        ArrayList<Folder_> folderArrayList = new ArrayList<>();
        if (response == null) {
            Folder_ file_ = new Folder_();
            file_.setStatus(12);
            folderArrayList.add(file_);
            return folderArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Folder_ file_ = new Folder_();
            file_.setStatus(13);
            folderArrayList.add(file_);
            return folderArrayList;
        }
        if (Error_.noData(response, "folder", _context) == 2) {
            Folder_ file_ = new Folder_();
            file_.setStatus(2);
            folderArrayList.add(file_);
            return folderArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, "folder");

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Folder_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, "folder").toString(), listType);
        }
        return folderArrayList;
    }

    public static ArrayList<AllFolder_> parseAllFolders(String response, Context _context, String TAG) {
        ArrayList<AllFolder_> folderArrayList = new ArrayList<>();
        if (response == null) {
            AllFolder_ allFolder_ = new AllFolder_();
            allFolder_.setStatus(12);
            folderArrayList.add(allFolder_);
            return folderArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            AllFolder_ allFolder_ = new AllFolder_();
            allFolder_.setStatus(13);
            folderArrayList.add(allFolder_);
            return folderArrayList;
        }
        if (Error_.noData(response, "all_folders", _context) == 2) {
            AllFolder_ allFolder_ = new AllFolder_();
            allFolder_.setStatus(2);
            folderArrayList.add(allFolder_);
            return folderArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, "all_folders");

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<AllFolder_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, "all_folders").toString(), listType);
        }
        return folderArrayList;
    }


    public static ArrayList<DashboardMessage_> parseDashboardMessage(String response, Context _context, String TAG) {
        ArrayList<DashboardMessage_> messageArrayList = new ArrayList<>();
        if (response == null) {
            DashboardMessage_ dashboardMessage_ = new DashboardMessage_();
            dashboardMessage_.setStatus(12);
            messageArrayList.add(dashboardMessage_);
            return messageArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            DashboardMessage_ dashboardMessage_ = new DashboardMessage_();
            dashboardMessage_.setStatus(13);
            messageArrayList.add(dashboardMessage_);
            return messageArrayList;
        }
        if (Error_.noData(response, Actions_.PLATFORM_MSG, _context) == 2) {
            DashboardMessage_ dashboardMessage_ = new DashboardMessage_();
            dashboardMessage_.setStatus(2);
            messageArrayList.add(dashboardMessage_);
            return messageArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.PLATFORM_MSG);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<DashboardMessage_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.PLATFORM_MSG).toString(), listType);
        }
        return messageArrayList;
    }

    public static ArrayList<Event_> parseEvents(String response, Context _context, String TAG) {
        ArrayList<Event_> eventArrayList = new ArrayList<>();
        if (response == null) {
            Event_ event_ = new Event_();
            event_.setStatus(12);
            eventArrayList.add(event_);
            return eventArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Event_ event_ = new Event_();
            event_.setStatus(13);
            eventArrayList.add(event_);
            return eventArrayList;
        }
        if (Error_.noData(response, Actions_.CALENDAR, _context) == 2) {
            Event_ event_ = new Event_();
            event_.setStatus(2);
            eventArrayList.add(event_);
            return eventArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.CALENDAR);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Event_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.CALENDAR).toString(), listType);
        } else {
            Event_ event_ = new Event_();
            event_.setStatus(11);
            eventArrayList.add(event_);
            return eventArrayList;
        }
    }

    public static ArrayList<Invitations_> parseInvite(String response, Context _context, String TAG) {
        ArrayList<Invitations_> invitationList = new ArrayList<>();
        if (response == null) {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(12);
            invitationList.add(invitations_);
            return invitationList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(13);
            invitationList.add(invitations_);
            return invitationList;
        }
        if (Error_.noData(response, Actions_.GET_INVITATIONS, _context) == 2) {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(2);
            invitationList.add(invitations_);
            return invitationList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.GET_INVITATIONS);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Invitations_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.GET_INVITATIONS).toString(), listType);
        } else {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(11);
            invitationList.add(invitations_);
            return invitationList;
        }
    }

    public static ArrayList<Invitations_> friendAccept(String response, Context _context, String TAG) {
        ArrayList<Invitations_> invitationList = new ArrayList<>();
        if (response == null) {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(12);
            invitationList.add(invitations_);
            return invitationList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(13);
            invitationList.add(invitations_);
            return invitationList;
        }
        if (Error_.noData(response, Actions_.ACCEPT, _context) == 2) {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(2);
            invitationList.add(invitations_);
            return invitationList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.ACCEPT);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Invitations_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.ACCEPT).toString(), listType);
        } else {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(11);
            invitationList.add(invitations_);
            return invitationList;
        }
    }

    public static ArrayList<Invitations_> friendDecline(String response, Context _context, String TAG) {
        ArrayList<Invitations_> invitationList = new ArrayList<>();
        if (response == null) {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(12);
            invitationList.add(invitations_);
            return invitationList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(13);
            invitationList.add(invitations_);
            return invitationList;
        }
        if (Error_.noData(response, Actions_.DECLINE, _context) == 2) {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(2);
            invitationList.add(invitations_);
            return invitationList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.DECLINE);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Invitations_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.DECLINE).toString(), listType);
        } else {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(11);
            invitationList.add(invitations_);
            return invitationList;
        }
    }


    public static ArrayList<Task_> parseTaskList(String response, Context _context, String TAG) {
        ArrayList<Task_> taskArrayList = new ArrayList<>();
        if (response == null) {
            Task_ task_ = new Task_();
            task_.setStatus(12);
            taskArrayList.add(task_);
            return taskArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Task_ task_ = new Task_();
            task_.setStatus(13);
            taskArrayList.add(task_);
            return taskArrayList;
        }
        if (Error_.noData(response, "task_list", _context) == 2) {
            Task_ task_ = new Task_();
            task_.setStatus(2);
            taskArrayList.add(task_);
            return taskArrayList;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Task_>>() {
        }.getType();
        taskArrayList = gson.fromJson(GetJson_.getArray(response, "task_list").toString(), listType);
        return taskArrayList;
    }

    public static ArrayList<MessageBoard_> parseMessageBoard(String response, String action, Context _context, String TAG) {
        ArrayList<MessageBoard_> messageBoardArrayList = new ArrayList<>();
        if (response == null) {
            MessageBoard_ messageBoard_ = new MessageBoard_();
            messageBoard_.setStatus(12);
            messageBoardArrayList.add(messageBoard_);
            return messageBoardArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            MessageBoard_ messageBoard_ = new MessageBoard_();
            messageBoard_.setStatus(13);
            messageBoardArrayList.add(messageBoard_);
            return messageBoardArrayList;
        }
        if (Error_.noData(response, action, _context) == 2) {
            MessageBoard_ messageBoard_ = new MessageBoard_();
            messageBoard_.setStatus(2);
            messageBoardArrayList.add(messageBoard_);
            return messageBoardArrayList;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<MessageBoard_>>() {
        }.getType();
        messageBoardArrayList = gson.fromJson(GetJson_.getArray(response, action).toString(), listType);
        return messageBoardArrayList;
    }
}
