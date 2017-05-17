package com.artemminakov.timemanager;


import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class DayTimetable {
    private ArrayList<Task> mTasks;

    private static DayTimetable sDayTimetable;
    private Date mDate;

    private DayTimetable() {
        mTasks = new ArrayList<>();
        mDate = new Date();

    }

    public static DayTimetable get() {
        if (sDayTimetable == null) {
            sDayTimetable = new DayTimetable();
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

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public void addTask(Task task) {
        if (mTasks.size() > 14) {
            return;
        }
        mTasks.add(task);
    }

    public void clear() {
        if (mTasks.isEmpty()) {
            return;
        }
        mTasks.clear();
    }
}
