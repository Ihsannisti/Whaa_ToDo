package com.tutorial.whaato_do.db;

import android.provider.BaseColumns;

// To define constants for the table name, column names, and SQL queries.

public class TaskContract {
    public static final class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TASK_NAME = "name";
    }
}