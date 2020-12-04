package com.storage.database.core;

import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.storage.database.constants.TableList_;

/**
 * @author Kailash Karankal
 */

/*
 * all database query to create all tables
 */

final class Create_ {

    static final String CREATE_TABLE_UPDATE_LOG = "CREATE TABLE IF NOT EXISTS "
            + TableList_.TABLE_UPDATE_LOG + "("
            + General.USER_ID + " INTEGER PRIMARY KEY,"
            + General.ANNOUNCEMENT + " VARCHAR(150),"
            + General.FEED + " VARCHAR(150),"
            + General.GROUP_TASK_LIST + " VARCHAR(150),"
            + General.TASK_LIST + " VARCHAR(150)" + ")";

    static final String CREATE_TABLE_ANNOUNCEMENT = "CREATE TABLE IF NOT EXISTS "
            + TableList_.TABLE_ANNOUNCEMENT + "("
            + General.ID + " INTEGER PRIMARY KEY,"
            + General.DESCRIPTION + " VARCHAR(500),"
            + General.LAST_UPDATED + " INTEGER(150),"
            + General.USERNAME + " VARCHAR(150),"
            + General.CREATED_BY + " VARCHAR(150),"
            + General.TEAM_NAME + " VARCHAR(200),"
            + General.IS_READ + " INTEGER(10),"
            + General.TEAM_ID + " INTEGER(150),"
            + General.IS_DELETE + " INTEGER(10),"
            + General.IMAGE + " VARCHAR(500)" + ")";

    static final String CREATE_TABLE_TASK_LIST = "CREATE TABLE IF NOT EXISTS "
            + TableList_.TABLE_TASK_LIST + "("
            + General.ID + " INTEGER PRIMARY KEY,"
            + General.DUE_DATE + " INTEGER(150),"
            + General.TITLE + " VARCHAR(250),"
            + General.DESCRIPTION + " VARCHAR(500),"
            + General.ADDED_BY + " VARCHAR(200),"
            + General.TEAM_NAME + " VARCHAR(200),"
            + General.TO_DO_STATUS + " VARCHAR(50),"
            + General.FULL_NAME + " VARCHAR(250),"
            + General.PRIORITY + " VARCHAR(150),"
            + General.TEAM_ID + " INTEGER(150),"
            + General.IS_READ + " INTEGER(10),"
            + General.IS_OWNER + " INTEGER(10),"
            + General.IS_DELETE + " INTEGER(10),"
            + General.OWN_OR_TEAM + " INTEGER(10),"
            + General.IMAGE + " VARCHAR(500)" + ")";

    static final String CREATE_TABLE_MESSAGES = "CREATE TABLE "
            + TableList_.TABLE_MESSAGES + "("
            + General.ID + " INTEGER PRIMARY KEY,"
            + Chat.NAME + " VARCHAR(200), "
            + Chat.TO + "_" + General.ID + " INTEGER(100), "
            + Chat.FROM + "_" + General.ID + " INTEGER(100),"
            + Chat.MESSAGE + " VARCHAR(500) ,"
            + Chat.SENT + " INTEGER(100), "
            + Chat.MESSAGE_TYPE + " INTEGER(10), "
            + General.IS_READ + " INTEGER(10), "
            + General.DOWNLOAD_STATUS + " INTEGER(10), "
            + Chat.SELF + " INTEGER(10)" + ")";

    static final String CREATE_TABLE_FRIENDS = "CREATE TABLE "
            + TableList_.TABLE_FRIENDS + "("
            + General.ID + " INTEGER PRIMARY KEY,"
            + Chat.NAME + " VARCHAR(250),"
            + Chat.STATUS + " VARCHAR(250),"
            + Chat.MESSAGE + " VARCHAR(350), "
            + Chat.PHOTO + " VARCHAR(350),"
            + Chat.CHANNEL + " VARCHAR(350),"
            + Chat.TYPING + " INTEGER(10),"
            + Chat.LAST_SEEN + " VARCHAR(50)" + ")";

    static final String CREATE_TABLE_ROOM = "CREATE TABLE "
            + TableList_.TABLE_CHATROOM + "("
            + General.ID + " INTEGER PRIMARY KEY,"
            + General.NAME + " VARCHAR(250),"
            + General.CREATED_BY + " VARCHAR(250),"
            + General.TYPE + " VARCHAR(350), "
            + General.COUNT + " VARCHAR(350),"
            + General.PASSWORD + " VARCHAR(50)" + ")";

    static final String CREATE_TABLE_ROOM_MESSAGE = "CREATE TABLE "
            + TableList_.TABLE_ROOM_MESSAGE + "("
            + General.ID + " INTEGER PRIMARY KEY,"
            + General.NAME + " VARCHAR(200), "
            + Chat.ROOM_ID + " INTEGER(100), "
            + Chat.FROM + "_" + General.ID + " INTEGER(100),"
            + Chat.MESSAGE + " VARCHAR(500) ,"
            + Chat.SENT + " INTEGER(100), "
            + Chat.MESSAGE_TYPE + " INTEGER(10), "
            + Chat.TEXT_COLOR + " VARCHAR(50), "
            + Chat.SELF + " INTEGER(10), "
            + General.DOWNLOAD_STATUS + " INTEGER(10)" + ")";

    static final String CREATE_TABLE_CHAT_SETTINGS = "CREATE TABLE "
            + TableList_.TABLE_CHAT_SETTINGS + "("
            + General.ID + " INTEGER PRIMARY KEY,"
            + Chat.NAME + " VARCHAR(250), "
            + Chat.MESSAGE + " VARCHAR(300) ,"
            + Chat.STATUS + " VARCHAR(300)" + ")";

    static final String CREATE_TABLE_EVENTS = "CREATE TABLE "
            + TableList_.TABLE_EVENTS + "("
            + General.ID + " INTEGER PRIMARY KEY,"
            + General.TIMESTAMP + " INTEGER(150),"
            + General.EVENT_TIME + " VARCHAR(250),"
            + General.NAME + " VARCHAR(150),"
            + General.DESCRIPTION + " VARCHAR(200),"
            + General.USERNAME + " VARCHAR(150),"
            + General.IMAGE + " VARCHAR(500),"
            + General.TEAM_ID + " INTEGER(150),"
            + General.TEAM_NAME + " VARCHAR(200),"
            + General.DATE_STRING + " VARCHAR(200),"
            + General.PARTICIPANTS + " INTEGER(10),"
            + General.IS_READ + " INTEGER(10),"
            + General.LOCATION + " VARCHAR(150),"
            + General.IS_DELETE + " INTEGER(10)" + ")";

    static final String CREATE_TABLE_FMS = "CREATE TABLE "
            + TableList_.TABLE_FMS + "("
            + General.ID + " INTEGER PRIMARY KEY,"
            + General.GROUP_ID + " INTEGER(150),"
            + General.USER_ID + " INTEGER(150),"
            + General.IS_READ + " INTEGER(10),"
            + General.IS_DEFAULT + " INTEGER(10),"
            + General.CHECK_IN + " INTEGER(10),"
            + General.PERMISSION + " INTEGER(10),"
            + General.POSTED_DATE + " INTEGER(200),"
            + General.TEAM_NAME + " VARCHAR(200),"
            + General.USERNAME + " VARCHAR(150),"
            + General.FULL_NAME + " VARCHAR(200),"
            + General.REAL_NAME + " VARCHAR(150),"
            + General.COMMENT + " VARCHAR(200),"
            + General.DESCRIPTION + " VARCHAR(200),"
            + General.SIZE + " INTEGER(150),"
            + General.IS_DELETE + " INTEGER(10)" + ")";

    static final String CREATE_TABLE_TEAM_TALK = "CREATE TABLE "
            + TableList_.TABLE_TEAM_TALK + "("
            + General.ID + " INTEGER PRIMARY KEY,"
            + General.TITLE + " VARCHAR(500),"
            + General.MESSAGE + " VARCHAR(500),"
            + General.TEAM_ID + " INTEGER(150),"
            + General.TEAM_NAME + " VARCHAR(200),"
            + General.ADDED_BY + " VARCHAR(200),"
            + General.FULL_NAME + " VARCHAR(200),"
            + General.IMAGE + " VARCHAR(500),"
            + General.LAST_UPDATED + " INTEGER(200),"
            + General.IS_READ + " INTEGER(10),"
            + General.COUNT + " VARCHAR(10),"
            + General.IS_DELETE + " INTEGER(10)" + ")";

    static final String CREATE_TABLE_COMETCHAT_TRACK = "CREATE TABLE "
            + TableList_.TABLE_COMETCHAT_TALK + "("
            + General.CHAT_ID + " INTEGER PRIMARY KEY,"
            + General.FROM_CHAT + " VARCHAR(500),"
            + General.TO_CHAT + " VARCHAR(500),"
            + General.COMET_CHAT_TYPE + " INTEGER(150),"
            + General.CHAT_MESSAGE_ID + " VARCHAR(200),"
            + General.CHAT_GROUP_ID + " VARCHAR(200),"
            + General.PRIVATE_GROUP_ID + " VARCHAR(200),"
            + General.CHAT_TYPE + " VARCHAR(500),"
            + General.READ_CHAT + " INTEGER(200)" + ")";



}
