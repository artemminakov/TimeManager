package com.artemminakov.timemanager;

import java.util.UUID;


public class Task {

    private static String[] taskTime = {"  8:00", "  9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
            "18:00", "19:00", "20:00", "21:00", "22:00"};

    private UUID mId;
    private String mTitle;
    private String mPriority;
    private int mNumberOfHoursToSolve;
    private boolean mIsSolved;
    private int mSpentOnSolution;

    public Task() {
        mId = UUID.randomUUID();
        mTitle = "Task " + mId;
        mPriority = "Обычный";
        mNumberOfHoursToSolve = 0;
        mIsSolved = false;
        mSpentOnSolution = 0;
    }

    public Task(String title, String priority, int quantityHours, boolean isSolved) {
        mId = UUID.randomUUID();
        mTitle = title;
        mPriority = priority;
        mNumberOfHoursToSolve = quantityHours;
        mIsSolved = isSolved;
        mSpentOnSolution = 0;
    }

    public String getId() {
        return mId.toString();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getPriority() {
        return mPriority;
    }

    public void setPriority(String mPriority) {
        this.mPriority = mPriority;
    }

    public int getNumberOfHoursToSolve() {
        return mNumberOfHoursToSolve;
    }

    public void setNumberOfHoursToSolve(int mNumberOfHoursToSolve) {
        this.mNumberOfHoursToSolve = mNumberOfHoursToSolve;
    }

    public boolean isSolved() {
        return mIsSolved;
    }

    public void setIsSolved(boolean mIsSolved) {
        this.mIsSolved = mIsSolved;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    public String getTaskTime(int position) {
        return taskTime[position];
    }

    public void setSpentOnSolution(int spentOnSolution){
        mSpentOnSolution = spentOnSolution;
    }

    public int getSpentOnSolution(){
        return mSpentOnSolution;
    }

}
