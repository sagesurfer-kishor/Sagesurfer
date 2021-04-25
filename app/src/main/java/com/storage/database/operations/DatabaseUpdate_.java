package com.storage.database.operations;

import android.content.Context;

import com.storage.database.core.MainDatabase;

/**
 * @author Kailash Karankal
 */

public final class DatabaseUpdate_ {
    private final MainDatabase mainDatabase;

    public DatabaseUpdate_(Context _context) {
        mainDatabase = new MainDatabase(_context);
    }

    //update read/unread of record with table name
    public final void updateRead(String columnName, String tableName, String value, String id) {
        mainDatabase.updateRead(columnName, tableName, value, id);
        mainDatabase.close();
    }

    public void updateFriend(String user_id, String tableName, String columnName, String value) {
        mainDatabase.updateFriend(user_id, tableName, columnName, value);
        mainDatabase.close();
    }

    public void updateMessage(String id, String message, String download) {
        mainDatabase.updateMessage(id, message, download);
        mainDatabase.close();
    }

    public void updateMessageRead(String id) {
        mainDatabase.updateMessageRead(id);
        mainDatabase.close();
    }
}
