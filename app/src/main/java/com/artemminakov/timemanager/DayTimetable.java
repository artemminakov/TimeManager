package com.artemminakov.timemanager;


import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class DayTimetable {
    private ArrayList<Task> mTasks;

    private static DayTimetable sDayTimetable;
    private Context mAppContext;
    private Date mDate;
    private boolean solvedTasks[] = {false, false, true, false, false, true, false, false, true, false, false, true, false, false, true};

    private DayTimetable(Context appContext) {
        mAppContext = appContext;
        mTasks = new ArrayList<Task>();
        mDate = new Date();

    }

    public static DayTimetable get(Context c) {
        if (sDayTimetable == null) {
            sDayTimetable = new DayTimetable(c.getApplicationContext());
        }
        return sDayTimetable;
    }

    public ArrayList<Task> getTasks() {
        return mTasks;
    }

    public Task getTask(UUID id) {
        for (Task t : mTasks) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    public Date getDate() {
        return mDate;
    }

    public boolean[] getSolvedTasks(){
        return solvedTasks;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public void addTask(Task task) {
        if (mTasks.size() > 14){
            return;
        }
        mTasks.add(task);
    }

    public void clear(){
        mTasks.clear();
    }
}
