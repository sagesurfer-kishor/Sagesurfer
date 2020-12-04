package com.storage.database.operations;

import android.content.Context;

import com.storage.database.core.MainDatabase;

/**
 * @author Kailash Karankal
 */

/*
* Make call to delete table with table name from database
*/

public final class DatabaseDeleteRecord_ {
    private final MainDatabase databaseHelper;

    public DatabaseDeleteRecord_(Context context) {
        databaseHelper = new MainDatabase(context);
    }

    public final void deleteRecord(String table_name, String id, String column) {
        databaseHelper.deleteEntry(table_name, id, column);
        databaseHelper.close();
    }
}
