package com.artemminakov.timemanager;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;


public class TaskLab {
    private ArrayList<Task> mTasks;

    private static TaskLab sTaskLab;
    private Context mAppContext;

    private TaskLab(Context appContext){
        mAppContext = appContext;
        mTasks = new ArrayList<Task>();

        for (int i =0; i < 11; i++){
            Task task = new Task();
            task.setTitle("Task №" + (i+1));
            task.setNumberOfHoursToSolve(i);
            task.setPriority("Обычный");
            task.setIsSolved(i % 2 == 0);
            mTasks.add(task);
        }
    }

    public static TaskLab get(Context c){
        if(sTaskLab == null){
            sTaskLab = new TaskLab(c.getApplicationContext());
        }
        return sTaskLab;
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

    public void addTask(Task task){
        mTasks.add(task);
    }
}
