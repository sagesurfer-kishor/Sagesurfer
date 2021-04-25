package com.storage.database.core;

import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.storage.database.constants.TableList_;

/**
 * @author Kailash Karankal
 */

/*
 * Database query to fetch values from database
 */

final class Retrieve_ {

    static String getUpdateLog(String columnName, String user_id) {
        return "SELECT " + columnName + " FROM " + TableList_.TABLE_UPDATE_LOG + " WHERE "
                + General.USER_ID + "=" + user_id;
    }

    public static String count(String table_name) {
        return "SELECT COUNT(*) AS COUNTS FROM " + table_name;
    }

    static String countWhere(String table_name, String whereCondition) {
        return "SELECT COUNT(*) AS COUNTS FROM " + table_name + " WHERE " + whereCondition;
    }

    static String getFriend(String id) {
        return "SELECT " + Chat.NAME + " FROM " + TableList_.TABLE_FRIENDS + " WHERE " + General.ID + " = " + id;
    }

    static String getUnreadCount(String id) {
        return "SELECT COUNT(" + General.IS_READ + ") FROM " + TableList_.TABLE_MESSAGES
                + " WHERE " + Chat.FROM + "_" + General.ID + " = " + id + " AND " + General.IS_READ + " = 0";
    }

    static String getMessages(String user_id, String friend_id) {
        return "SELECT  * FROM "
                + TableList_.TABLE_MESSAGES + " WHERE (("
                + Chat.FROM + "_" + General.ID + " = " + user_id + " AND "
                + Chat.TO + "_" + General.ID + " = " + friend_id + ") OR ("
                + Chat.FROM + "_" + General.ID + " = " + friend_id
                + " AND " + Chat.TO + "_" + General.ID + " = " + user_id
                + ")) " + "ORDER BY " + General.ID
                + " DESC LIMIT 20";
    }
}
