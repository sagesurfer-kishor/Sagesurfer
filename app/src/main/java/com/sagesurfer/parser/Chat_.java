package com.sagesurfer.parser;

import android.content.Context;
import android.database.Cursor;

import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.TimeConverter;
import com.sagesurfer.library.TimeSet;
import com.storage.database.constants.TableList_;
import com.storage.database.operations.DatabaseInsert_;
import com.storage.database.operations.DatabaseRetrieve_;
import com.storage.database.operations.DatabaseUpdate_;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class Chat_ {

    private static DatabaseInsert_ insertOperations;
    private static DatabaseRetrieve_ retrieveOperations;

    public static ArrayList<HashMap<String, String>> friendsFromDb(Cursor mCursor, Context context) {
        retrieveOperations = new DatabaseRetrieve_(context);

        ArrayList<HashMap<String, String>> friendsList = new ArrayList<>();
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                while (!mCursor.isAfterLast()) {
                    HashMap<String, String> map = new HashMap<>();
                    String user_id = mCursor.getString(mCursor.getColumnIndex(General.ID));
                    map.put(General.ID, user_id);
                    map.put(General.COUNT, "" + retrieveOperations.getUnreadCount(user_id));
                    map.put(Chat.STATUS, mCursor.getString(mCursor.getColumnIndex(Chat.STATUS)));
                    map.put(Chat.MESSAGE, mCursor.getString(mCursor.getColumnIndex(Chat.MESSAGE)));
                    map.put(Chat.PHOTO, mCursor.getString(mCursor.getColumnIndex(Chat.PHOTO)));
                    map.put(Chat.NAME, mCursor.getString(mCursor.getColumnIndex(Chat.NAME)));
                    map.put(Chat.LAST_SEEN, mCursor.getString(mCursor.getColumnIndex(Chat.LAST_SEEN)));
                    map.put(Chat.CHANNEL, mCursor.getString(mCursor.getColumnIndex(Chat.CHANNEL)));
                    map.put(Chat.TYPING, mCursor.getString(mCursor.getColumnIndex(Chat.TYPING)));

                    friendsList.add(map);
                    mCursor.moveToNext();
                }
            }
        }
        Collections.reverse(friendsList);
        return friendsList;
    }

    public static void friends(JSONObject onlineUser, Context context) {
        insertOperations = new DatabaseInsert_(context);
        retrieveOperations = new DatabaseRetrieve_(context);

        Iterator<String> keys = onlineUser.keys();
        while (keys.hasNext()) {
            try {
                JSONObject user = onlineUser.getJSONObject(keys.next());
                if (user.getInt(General.GID) == 0 && user.getInt(Chat.PRO_TYPE) == 0) {

                    String username = user.getString(Chat.NAME);
                    String status_message = user.getString(Chat.MESSAGE);
                    String image = user.getString(Chat.PHOTO);
                    String id = user.getString(General.ID);
                    String status = user.getString(Chat.STATUS);
                    String last_seen = user.getString(Chat.LAST_SEEN);
                    String channel = user.getString(Chat.CHANNEL);

                    if (Integer.parseInt(id) != 1) {
                        insertOperations.insertFriend(username, status_message, image, id, status, last_seen, channel);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<HashMap<String, String>> roomsFromDb(Cursor mCursor) {
        ArrayList<HashMap<String, String>> roomsList = new ArrayList<>();
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                while (!mCursor.isAfterLast()) {
                    HashMap<String, String> map = new HashMap<>();

                    map.put(General.ID, mCursor.getString(mCursor.getColumnIndex(General.ID)));
                    map.put(General.NAME, mCursor.getString(mCursor.getColumnIndex(General.NAME)));
                    map.put(General.CREATED_BY, mCursor.getString(mCursor.getColumnIndex(General.CREATED_BY)));
                    map.put(General.TYPE, mCursor.getString(mCursor.getColumnIndex(General.TYPE)));
                    map.put(General.COUNT, mCursor.getString(mCursor.getColumnIndex(General.COUNT)));
                    map.put(General.PASSWORD, mCursor.getString(mCursor.getColumnIndex(General.PASSWORD)));

                    roomsList.add(map);
                    mCursor.moveToNext();
                }
            }
        }
        Collections.reverse(roomsList);
        return roomsList;
    }

    public static ArrayList<HashMap<String, String>> historyMessage(JSONObject jsonObject, Context context) {
        insertOperations = new DatabaseInsert_(context);
        ArrayList<HashMap<String, String>> messageList = new ArrayList<>();
        long result;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("history");
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject messageObject = jsonArray.getJSONObject(i);
                    String id = messageObject.getString(General.ID);
                    String message = messageObject.getString(General.MESSAGE);
                    String message_type = messageObject.getString(Chat.MESSAGE_TYPE);
                    if (Integer.parseInt(message_type) == 12) {
                        message_type = "13";
                    }
                    String from = Preferences.get(General.USER_ID);
                    if (messageObject.has(Chat.TO)) {
                        from = messageObject.getString(Chat.FROM);
                    }

                    String sent = messageObject.getString(Chat.SENT);
                    String self = messageObject.getString(Chat.SELF);
                    String name = "Me";
                    if (Integer.parseInt(self) != 1) {
                        name = retrieveOperations.getFriendDetails(from);
                    }
                    String to = Preferences.get(General.USER_ID);
                    if (messageObject.has(Chat.TO)) {
                        to = messageObject.getString(Chat.TO);
                    }
                    String is_read = "1";
                    String download = "1";

                    HashMap<String, String> messageMap = new HashMap<>();
                    messageMap.put(General.ID, id);
                    messageMap.put(Chat.MESSAGE, message);
                    messageMap.put(Chat.FROM, from);
                    messageMap.put(Chat.TO, to);
                    messageMap.put(Chat.NAME, name);
                    messageMap.put(Chat.SELF, self);
                    messageMap.put(Chat.SENT, sent);
                    messageMap.put(General.IS_READ, is_read);
                    messageMap.put(Chat.MESSAGE_TYPE, message_type);
                    messageMap.put(General.DOWNLOAD_STATUS, download);

                    result = insertOperations.insertMessage(id, to, from, self, is_read, message,
                            name, sent, message_type, download);
                    if (result > 1) {
                        messageList.add(messageMap);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    public static ArrayList<HashMap<String, String>> chatroom(JSONObject chatroom, Context context) {
        insertOperations = new DatabaseInsert_(context);
        ArrayList<HashMap<String, String>> chatroomList = new ArrayList<>();
        Iterator<String> keys = chatroom.keys();
        while (keys.hasNext()) {
            try {
                JSONObject chatroomObject = chatroom.getJSONObject(keys.next());

                if (chatroomObject.getInt(General.GID) != 0) {
                    String id = chatroomObject.getString(General.ID);
                    String name = chatroomObject.getString(Chat.NAME);
                    String counter = chatroomObject.getString("online_cnt");
                    //String created_by = chatroomObject.getString("createdby");
                    //String type = chatroomObject.getString(General.TYPE);
                    //String password = chatroomObject.getString("i");

                    HashMap<String, String> chatroomMap = new HashMap<>();
                    chatroomMap.put(General.ID, id);
                    chatroomMap.put(General.NAME, name);
                    //chatroomMap.put(General.CREATED_BY, created_by);
                    //chatroomMap.put(General.TYPE, type);
                    chatroomMap.put(General.COUNT, counter);
                    //chatroomMap.put(General.PASSWORD, password);

                    chatroomList.add(chatroomMap);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return chatroomList;
    }

    public static HashMap<String, String> myMessage(JSONObject messageObject, String from,
                                                    String to, String type, Context context) {
        insertOperations = new DatabaseInsert_(context);
        HashMap<String, String> messageMap = new HashMap<>();
        long result = 0;
        try {
            String id = messageObject.getString(General.ID);
            String self = "1";
            String is_read = "1";
            String message = "";
            String download = "0";
            if (Integer.parseInt(type) == Integer.parseInt(Chat.TYPE_MESSAGE)) {
                message = messageObject.getString(Chat.MESSAGE);
            }
            if (Integer.parseInt(type) == Integer.parseInt(Chat.TYPE_IMAGE)
                    || Integer.parseInt(type) == Integer.parseInt(Chat.TYPE_VIDEO)
                    || Integer.parseInt(type) == Integer.parseInt(Chat.TYPE_AUDIO)
                    || Integer.parseInt(type) == Integer.parseInt(Chat.TYPE_DOCUMENT)) {
                message = messageObject.getString(Chat.ORIGINAL_FILE);
            }

            String name = "Me";
            String sent = TimeConverter.getCurrentTime();

            messageMap.put(General.ID, id);
            messageMap.put(Chat.MESSAGE, message);
            messageMap.put(Chat.FROM, from);
            messageMap.put(Chat.TO, to);
            messageMap.put(Chat.NAME, name);
            messageMap.put(Chat.SELF, self);
            messageMap.put(Chat.SENT, sent);
            messageMap.put(General.IS_READ, is_read);
            messageMap.put(Chat.MESSAGE_TYPE, type);
            messageMap.put(General.DOWNLOAD_STATUS, download);

            result = insertOperations.insertMessage(id, to, from, self, is_read, message, name, sent, type, download);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result > 0) {
            return messageMap;
        }
        return null;
    }

    public static HashMap<String, String> avChatMessage(JSONObject jsonObject, Context context) {
        Preferences.initialize(context);
        retrieveOperations = new DatabaseRetrieve_(context);
        insertOperations = new DatabaseInsert_(context);

        long result = 0;
        HashMap<String, String> messageMap = new HashMap<>();
        try {
            String id = jsonObject.getString(General.ID);
            String self = jsonObject.getString(Chat.SELF);
            String user_id = Preferences.get(General.USER_ID);
            String from_id = jsonObject.getString(Chat.FROM);

            String image;
            String name;
            if (Integer.parseInt(self) == 1) {
                name = retrieveOperations.getFriendDetails(from_id);
                user_id = jsonObject.getString(Chat.TO);
                image = jsonObject.getString("iconSelf");
            } else {
                image = jsonObject.getString("icon");
                name = jsonObject.getString(General.NAME);
            }
            String message = jsonObject.getString(General.MESSAGE);
            String message_type = jsonObject.getString(Chat.MESSAGE_TYPE);
            String sent = jsonObject.getString(Chat.SENT);
            String plugin_type = jsonObject.getString(Chat.PLUGIN_TYPE);
            String is_read = "1";
            String download = "0";
            String call_id;
            if (jsonObject.has("callid")) {
                call_id = jsonObject.getString("callid");
                Preferences.save(Chat.CALL_ID, call_id);
            }

            messageMap.put(General.ID, id);
            messageMap.put(Chat.TO, user_id);
            messageMap.put(Chat.SELF, self);
            messageMap.put(General.MESSAGE, message);
            messageMap.put(General.NAME, name);
            messageMap.put(Chat.FROM, from_id);
            messageMap.put(Chat.MESSAGE_TYPE, message_type);
            messageMap.put(Chat.SENT, sent);
            messageMap.put(Chat.PHOTO, image);
            messageMap.put(Chat.PLUGIN_TYPE, plugin_type);

            result = insertOperations.insertMessage(id, user_id, from_id, self, is_read, message,
                    name, sent, message_type, download);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result > 0) {
            return messageMap;
        }
        return null;
    }

    public static long fromMessageEnter(String jsonString, String user_id, Context context) {

        insertOperations = new DatabaseInsert_(context);
        retrieveOperations = new DatabaseRetrieve_(context);

        long result = 0;
        try {
            JSONObject messageObject = new JSONObject(jsonString);
            String id = messageObject.getString(General.ID);
            String from = messageObject.getString(Chat.FROM);
            String name = retrieveOperations.getFriendDetails(from);
            String message = messageObject.getString(General.MESSAGE);
            String self = "0";
            String is_read = "0";
            String download = "0";
            String sent = messageObject.getString(Chat.SENT);
            String message_type = messageObject.getString(Chat.MESSAGE_TYPE);

            if (Integer.parseInt(message_type) == Chat.INTENT_IMAGE_RECEIVED) {
                download = "1";
                message_type = Chat.TYPE_IMAGE;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_VIDEO) {
                download = "1";
                message_type = Chat.TYPE_VIDEO;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_AUDIO) {
                download = "1";
                message_type = Chat.TYPE_AUDIO;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_DOCUMENT) {
                download = "1";
                message_type = Chat.TYPE_DOCUMENT;
            }

            result = insertOperations.insertMessage(id, user_id, from, self, is_read, message,
                    name, sent, message_type, download);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> fromMessage(String jsonString, String user_id) {
        HashMap<String, String> messageMap = new HashMap<>();
        try {
            JSONObject messageObject = new JSONObject(jsonString);
            String id = messageObject.getString(General.ID);
            String from = messageObject.getString(Chat.FROM);
            String message = messageObject.getString(General.MESSAGE);
            String self = "0";
            String is_read = "0";
            String download = "0";
            String name = "NA";
            String sent = messageObject.getString(Chat.SENT);
            String message_type = messageObject.getString(Chat.MESSAGE_TYPE);

            if (Integer.parseInt(message_type) == Chat.INTENT_IMAGE_RECEIVED) {
                download = "1";
                message_type = Chat.TYPE_IMAGE;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_VIDEO) {
                download = "1";
                message_type = Chat.TYPE_VIDEO;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_AUDIO) {
                download = "1";
                message_type = Chat.TYPE_AUDIO;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_DOCUMENT) {
                download = "1";
                message_type = Chat.TYPE_DOCUMENT;
            }
            messageMap.put(General.ID, id);
            messageMap.put(Chat.MESSAGE, message);
            messageMap.put(Chat.FROM, from);
            messageMap.put(Chat.TO, user_id);
            messageMap.put(Chat.NAME, name);
            messageMap.put(Chat.SELF, self);
            messageMap.put(Chat.SENT, sent);
            messageMap.put(General.IS_READ, is_read);
            messageMap.put(General.DOWNLOAD_STATUS, download);
            messageMap.put(Chat.MESSAGE_TYPE, message_type);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messageMap;
    }

    public static ArrayList<HashMap<String, String>> dbMyMessages(Cursor mCursor) {
        ArrayList<HashMap<String, String>> messageList = new ArrayList<>();
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                while (!mCursor.isAfterLast()) {
                    HashMap<String, String> map = new HashMap<>();

                    map.put(General.ID, mCursor.getString(mCursor.getColumnIndex(General.ID)));
                    map.put(Chat.TO, mCursor.getString(mCursor.getColumnIndex(Chat.TO + "_" + General.ID)));
                    map.put(Chat.FROM, mCursor.getString(mCursor.getColumnIndex(Chat.FROM + "_" + General.ID)));
                    map.put(Chat.SENT, mCursor.getString(mCursor.getColumnIndex(Chat.SENT)));
                    map.put(Chat.MESSAGE, mCursor.getString(mCursor.getColumnIndex(Chat.MESSAGE)));
                    map.put(Chat.SELF, mCursor.getString(mCursor.getColumnIndex(Chat.SELF)));
                    map.put(Chat.NAME, mCursor.getString(mCursor.getColumnIndex(Chat.NAME)));
                    map.put(General.IS_READ, mCursor.getString(mCursor.getColumnIndex(General.IS_READ)));
                    map.put(General.DOWNLOAD_STATUS, mCursor.getString(mCursor.getColumnIndex(General.DOWNLOAD_STATUS)));
                    map.put(Chat.MESSAGE_TYPE, mCursor.getString(mCursor.getColumnIndex(Chat.MESSAGE_TYPE)));

                    messageList.add(map);
                    mCursor.moveToNext();
                }
            }
        }
        Collections.reverse(messageList);
        return messageList;
    }

    public static HashMap<String, String> roomSentMessage(JSONObject jsonObject,
                                                          String type, String room_id,
                                                          Context context) {
        insertOperations = new DatabaseInsert_(context);
        try {
            String id = jsonObject.getString(General.ID);
            String sent = TimeSet.getChatTimestamp();
            String fromid = Preferences.get(General.USER_ID);
            String name = Preferences.get(General.NAME);
            String message_type = type;
            String text_color = "#000000";
            String self = "1";

            String download = "0";
            String message = "NA";

            if (Integer.parseInt(message_type) == Chat.INTENT_MESSAGE) {
                message = jsonObject.getString(Chat.MESSAGE);
            }

            if (Integer.parseInt(message_type) == Chat.INTENT_IMAGE) {
                message = jsonObject.getString(Chat.ORIGINAL_FILE);
                message_type = Chat.TYPE_IMAGE;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_VIDEO) {
                message = jsonObject.getString(Chat.ORIGINAL_FILE);
                message_type = Chat.TYPE_VIDEO;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_AUDIO) {
                message = jsonObject.getString(Chat.ORIGINAL_FILE);
                message_type = Chat.TYPE_AUDIO;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_DOCUMENT) {
                message = jsonObject.getString(Chat.ORIGINAL_FILE);
                message_type = Chat.TYPE_DOCUMENT;
            }
            HashMap<String, String> messageMap = new HashMap<>();
            messageMap.put(General.ID, id);
            messageMap.put(General.NAME, name);
            messageMap.put(Chat.ROOM_ID, room_id);
            messageMap.put(Chat.FROM, fromid);
            messageMap.put(Chat.MESSAGE, message);
            messageMap.put(Chat.SENT, sent);
            messageMap.put(Chat.MESSAGE_TYPE, message_type);
            messageMap.put(Chat.TEXT_COLOR, text_color);
            messageMap.put(General.DOWNLOAD_STATUS, download);
            messageMap.put(Chat.SELF, self);

            long result = insertOperations.insertRoomMessage(id, room_id, fromid, message, name, sent,
                    message_type, text_color, download, self);

            if (result != 1) {
                return messageMap;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<HashMap<String, String>> roomDbMessage(Cursor mCursor) {
        ArrayList<HashMap<String, String>> messageList = new ArrayList<>();
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                while (!mCursor.isAfterLast()) {
                    HashMap<String, String> map = new HashMap<>();

                    map.put(General.ID, mCursor.getString(mCursor.getColumnIndex(General.ID)));
                    map.put(General.NAME, mCursor.getString(mCursor.getColumnIndex(General.NAME)));
                    map.put(Chat.ROOM_ID, mCursor.getString(mCursor.getColumnIndex(Chat.ROOM_ID)));
                    map.put(Chat.FROM, mCursor.getString(mCursor.getColumnIndex(Chat.FROM + "_" + General.ID)));
                    map.put(Chat.MESSAGE, mCursor.getString(mCursor.getColumnIndex(Chat.MESSAGE)));
                    map.put(Chat.SENT, mCursor.getString(mCursor.getColumnIndex(Chat.SENT)));
                    map.put(Chat.SELF, mCursor.getString(mCursor.getColumnIndex(Chat.SELF)));
                    map.put(Chat.MESSAGE_TYPE, mCursor.getString(mCursor.getColumnIndex(Chat.MESSAGE_TYPE)));
                    map.put(Chat.TEXT_COLOR, mCursor.getString(mCursor.getColumnIndex(Chat.TEXT_COLOR)));
                    map.put(General.DOWNLOAD_STATUS, mCursor.getString(mCursor.getColumnIndex(General.DOWNLOAD_STATUS)));

                    messageList.add(map);
                    mCursor.moveToNext();
                }
            }
        }
        return messageList;
    }

    public static long roomReceivedMessageEnter(String json, Context context) {
        insertOperations = new DatabaseInsert_(context);
        try {
            JSONObject jsonObject = new JSONObject(json);
            String id = jsonObject.getString(General.ID);
            String message = jsonObject.getString(General.MESSAGE);
            String sent = jsonObject.getString(Chat.SENT);
            String fromid = jsonObject.getString("fromid");
            String name = "Unknown";
            if (jsonObject.has("from")) {
                name = jsonObject.getString(Chat.FROM);
            }
            String message_type = jsonObject.getString(Chat.MESSAGE_TYPE);
            String text_color = "#000000";
            String chatroomid = jsonObject.getString("chatroomid");
            String download = "0";
            String self = "0";
            if (Integer.parseInt(fromid) == Integer.parseInt(Preferences.get(General.USER_ID))) {
                self = "1";
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_IMAGE_RECEIVED) {
                download = "1";
                message_type = Chat.TYPE_IMAGE;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_VIDEO) {
                download = "1";
                message_type = Chat.TYPE_VIDEO;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_AUDIO) {
                download = "1";
                message_type = Chat.TYPE_AUDIO;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_DOCUMENT) {
                download = "1";
                message_type = Chat.TYPE_DOCUMENT;
            }

            if (Integer.parseInt(self) == 0) {
                return insertOperations.insertRoomMessage(id, chatroomid, fromid, message, name, sent,
                        message_type, text_color, download, self);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static HashMap<String, String> roomReceivedMessage(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String id = jsonObject.getString(General.ID);
            String message = jsonObject.getString(General.MESSAGE);
            String sent = jsonObject.getString(Chat.SENT);
            String fromid = jsonObject.getString("fromid");
            String name = "Unknown";
            if (jsonObject.has("from")) {
                name = jsonObject.getString(Chat.FROM);
            }
            String message_type = jsonObject.getString(Chat.MESSAGE_TYPE);
            String text_color = "#000000";
            String chatroomid = jsonObject.getString("chatroomid");
            String download = "0";
            String self = "0";
            String roomName = "";
            if (jsonObject.has("roomName")) {
                roomName = jsonObject.getString("roomName");
            }
            if (Integer.parseInt(fromid) == Integer.parseInt(Preferences.get(General.USER_ID))) {
                self = "1";
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_IMAGE_RECEIVED) {
                download = "1";
                message_type = Chat.TYPE_IMAGE;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_VIDEO) {
                download = "1";
                message_type = Chat.TYPE_VIDEO;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_AUDIO) {
                download = "1";
                message_type = Chat.TYPE_AUDIO;
            }
            if (Integer.parseInt(message_type) == Chat.INTENT_DOCUMENT) {
                download = "1";
                message_type = Chat.TYPE_DOCUMENT;
            }
            HashMap<String, String> messageMap = new HashMap<>();
            messageMap.put(General.ID, id);
            messageMap.put(General.NAME, name);
            messageMap.put(Chat.ROOM_ID, chatroomid);
            messageMap.put(Chat.FROM, fromid);
            messageMap.put(Chat.MESSAGE, message);
            messageMap.put(Chat.SENT, sent);
            messageMap.put(Chat.MESSAGE_TYPE, message_type);
            messageMap.put(Chat.TEXT_COLOR, text_color);
            messageMap.put(General.DOWNLOAD_STATUS, download);
            messageMap.put(Chat.SELF, self);
            messageMap.put("roomName", roomName);

            return messageMap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void settings(JSONObject jsonObject, Context context) {
        insertOperations = new DatabaseInsert_(context);
        try {
            if (jsonObject != null) {
                String id = jsonObject.getString(General.ID);
                String status = jsonObject.getString(Chat.STATUS);
                String status_message = jsonObject.getString(Chat.MESSAGE);
                String name = jsonObject.getString(Chat.NAME);

                insertOperations.insertSetting(id, name, status_message, status);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void actionMessage(JSONObject jsonObject, Context context) {
        DatabaseUpdate_ updateOperations = new DatabaseUpdate_(context);
        if (jsonObject.has(General.ACTION)) {
            try {
                String action = jsonObject.getString(General.ACTION);
                if (action.equalsIgnoreCase("typing_start")) {
                    String friend_id = jsonObject.getString(Chat.FROM);
                    updateOperations.updateFriend(friend_id, TableList_.TABLE_FRIENDS, Chat.TYPING, "1");
                }
                if (action.equalsIgnoreCase("typing_stop")) {
                    String friend_id = jsonObject.getString(Chat.FROM);
                    updateOperations.updateFriend(friend_id, TableList_.TABLE_FRIENDS, Chat.TYPING, "0");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
