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

    private DayTimetable(Context appContext){
        mAppContext = appContext;
        mTasks = new ArrayList<Task>();
        mDate = new Date();

        for (int i =0; i < 10; i++){
            Task task = new Task();
            task.setTitle("Task №" + (i+1));
            task.setNumberOfHoursToSolve(i);
            task.setPriority(i);
            task.setIsSolved(i % 1 == 0);
            mTasks.add(task);
        }
    }

    public static DayTimetable get(Context c){
        if(sDayTimetable == null){
            sDayTimetable = new DayTimetable(c.getApplicationContext());
        }
        return sDayTimetable;
    }

    public ArrayList<Task> getTasks() {
        return mTasks;
    }

    public Task getTask(UUID id){
        for (Task t : mTasks){
            if (t.getId().equals(id)){
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
}
