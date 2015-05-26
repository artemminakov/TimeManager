package com.artemminakov.timemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "myLogs";

    private static final String DB_NAME = "timtmanager.sqlite";
    private static final int VERSION = 1;

    public TaskDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate database ---");
        db.execSQL("create table task (idTask integer primary key autoincrement, title text, priority text, " +
                "quantityHours integer, isSolved integer);");

        db.execSQL("create table timetable (idTimetable integer primary key autoincrement, date text, taskId1 integer, " +
                "taskId2 integer, taskId3 integer, taskId4 integer, taskId5 integer, taskId6 integer, taskId7 integer, " +
                "taskId8 integer, taskId9 integer, taskId10 integer, taskId11 integer, taskId12 integer, taskId13 integer, " +
                "taskId14 integer, taskId15 integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
