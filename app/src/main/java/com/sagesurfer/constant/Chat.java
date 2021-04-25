package com.sagesurfer.constant;

import com.sagesurfer.network.Urls_;
import com.storage.preferences.Preferences;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class Chat {

    public static final String NAME = "n";
    public static final String PHOTO = "a";
    public static final String MESSAGE = "m";
    public static final String STATUS = "s";
    public static final String LAST_SEEN = "ls";
    public static final String PRO_TYPE = "proType";
    public static final String MESSAGE_TYPE = "message_type";
    public static final String FROM = "from";
    public static final String TYPING = "typing";
    public static final String SELF = "self";
    public static final String TO = "to";
    public static final String SENT = "sent";
    public static final String ROOM_ID = "room_id";
    public static final String TEXT_COLOR = "text_color";
    public static final String ORIGINAL_FILE = "original_file";
    public static final String PLUGIN_TYPE = "pluginType";
    public static final String CHANNEL = "ch";
    public static final String LINK = "l";
    /**
     * <b>NAME</b>: name of chatroom
     */
    public static final String GROUP_NAME = "name";
    /**
     * <b>I password of chatroom
     */
    public static final String GROUP_PASSWORD = "i";
    /**
     * <b>TYPE</b>: Type of chatroom (public,password-protected,invite only)
     */
    public static final String TYPE = "type";
    /**
     * <b>ONLINE</b>: No of online users
     */
    public static final String ONLINE = "online";
    public static final String CREATED_BY = "s";
    public static final String LSTN = "lstn";
    public static final String GROUP_STATUS = "j";
    public static final String GROUP_OWNER = "owner";
    public static final String LOCAL_MESSAGE_ID = "localmessageid";
    public static final String FROM_ID = "fromid";
    public static final String CHATROOM_ID = "chatroomid";
    public static final String OLD = "old";
    public static final String SENT_TIMESTAMP = "sent";
    public static final String WINDOW_ID = "window_id";
    public static final String GRP_WINDOW_ID = "grp_window_id";

    public static final String TYPE_MESSAGE = "10";
    public static final String TYPE_IMAGE = "13";
    public static final String TYPE_VIDEO = "14";
    public static final String TYPE_AUDIO = "16";
    public static final String TYPE_DOCUMENT = "17";
    public static final String TYPE_FILE = "17";

    public static final int INTENT_MESSAGE = 10;
    public static final int INTENT_INVITE = 11;
    public static final int INTENT_IMAGE_RECEIVED = 12;
    public static final int INTENT_IMAGE = 13;
    public static final int INTENT_VIDEO = 14;
    public static final int INTENT_AUDIO = 16;
    public static final int INTENT_DOCUMENT = 17;
    public static final int INTENT_FILE = 17;
    public static final int INTENT_CAMERA = 20;

    public static final int AVCHAT_CALL_ACCEPTED = 31;
    public static final int AVCHAT_INCOMING_CALL = 32;
    public static final int AVCHAT_BUSY_TONE = 33;
    public static final int AVCHAT_END_CALL = 34;
    public static final int AVCHAT_REJECT_CALL = 35;
    public static final int AVCHAT_CANCEL_CALL = 36;
    public static final int AVCHAT_NO_ANSWER = 37;
    public static final int AVCHAT_BUSY_CALL = 38;

    public static final String IS_ROOM = "is_room";
    public static final String CALL_ID = "call_id";
    public static final String CHAT_TO_DOWNLOAD = "1";
    public static final String CHAT_ON_DOWNLOAD = "0";
    public static final String CHAT_DOWNLOADING = "2";

    public static final String CHAT_URL = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.COMETCHAT_URL;
    //"https://replica.sagesurfer.com/cometchat/";
    //"https://ndesign.sagesurfer.com/cometchat7.0.36/";
    //"https://design.sagesurfer.com/cometchat7.0.36/";
    //"https://heartandsoul.sagesurfer.com/cometchat7.0.36/";
    public static final String CHAT_LICENCE_KEY = "ZE3EP-MIIU8-ZWBM4-M6DFN-MPVE2";
    public static final String CHAT_API_KEY = "c73dfefd0c13578abf61ff86b4eede43";
}
