package com.artemminakov.timemanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;


public class TaskLab {
    private ArrayList<Task> mTasks;

    private static TaskLab sTaskLab;

    private TaskLab() {
        mTasks = new ArrayList<>();
    }

    public static TaskLab get() {
        if (sTaskLab == null) {
            sTaskLab = new TaskLab();
        }
        return sTaskLab;
    }

    public ArrayList<Task> getTasks() {
        Collections.reverse(mTasks);
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

    public void addTask(Task task) {
        mTasks.add(task);
    }

    public void clear() {
        mTasks.clear();
    }

    public void deleteTask(Task task) {
        mTasks.remove(task);
    }
}
