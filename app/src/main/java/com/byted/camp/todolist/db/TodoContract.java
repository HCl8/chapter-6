package com.byted.camp.todolist.db;

import android.app.admin.SystemUpdateInfo;
import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + noteEntry.TABLE_NAME + " (" +
                    noteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    noteEntry.TABLE_DATE + " INTEGER," +
                    noteEntry.TABLE_STATE + " INTEGER," +
                    noteEntry.TABLE_PRIORITY + " INTEGER," +
                    noteEntry.TABLE_CONTENT + " text)";

    private TodoContract() {
    }

    public static class noteEntry implements BaseColumns{
        public static final String TABLE_NAME = "notes_P";
        public static final String TABLE_DATE = "date";
        public static final String TABLE_STATE = "state";
        public static final String TABLE_CONTENT = "content";
        public static final String TABLE_PRIORITY = "PRIORITY";
    }
}
