package com.example.justdoit.task_database;

import android.provider.BaseColumns;

public class TaskContract {

    public static final String DB_NAME = "com.rajdip.todolist.db";

    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns{

        public static final String TABLE = "tasks";

        public static final String TASK_TITLE = "title";
    }
}
