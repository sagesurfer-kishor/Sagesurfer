package com.storage.database.core;

import com.sagesurfer.constant.General;
import com.storage.database.constants.TableList_;

/**
 * @author Kailash Karankal
 */

/*
 * database query to insert value to table
 */

final class Insert_ {
    static String insertUpdateLog(String columnName, String utc, String user_id) {
        return "INSERT INTO " + TableList_.TABLE_UPDATE_LOG + " (" + columnName + ","
                + General.USER_ID + ") VALUES('" + utc + "', '" + user_id + "');";
    }
}
