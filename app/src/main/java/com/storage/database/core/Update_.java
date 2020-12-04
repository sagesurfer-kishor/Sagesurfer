package com.storage.database.core;

import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.storage.database.constants.TableList_;

/**
 * @author Kailash Karankal
 */

/*
 * database query to update values
 */

final class Update_ {

    static String updateLog(String columnName, String utc, String user_id) {
        return "UPDATE " + TableList_.TABLE_UPDATE_LOG + " SET "
                + columnName + "='" + utc + "' WHERE " + General.USER_ID + "=" + user_id;
    }

    static String updateRead(String columnName, String tableName, String value, String id) {
        return "UPDATE " + tableName + " SET "
                + columnName + " = " + value + " WHERE " + General.ID + " = " + id;
    }

    static String updateFriend(String user_id, String tableName, String columnName, String value) {
        return "UPDATE " + tableName + " SET "
                + columnName + " = " + value + " WHERE " + General.ID + " = " + user_id;
    }

    static String updateMessage(String id, String message, String download) {
        return "UPDATE " + TableList_.TABLE_MESSAGES + " SET "
                + General.DOWNLOAD_STATUS + " = " + download + " , "
                + Chat.MESSAGE + " = '" + message
                + "' WHERE " + General.ID + " = " + id;
    }

    static String updateMessageRead(String id) {
        return "UPDATE " + TableList_.TABLE_MESSAGES + " SET " + General.IS_READ
                + " = 1 WHERE " + General.ID + " = " + id;
    }
}
