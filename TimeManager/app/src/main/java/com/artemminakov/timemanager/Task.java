package com.artemminakov.timemanager;

import java.util.UUID;


public class Task {

    private UUID mId;
    private String mTitle;
    private int mPriority;
    private int mNumberOfHoursToSolve;
    private boolean mIsSolved;

    public Task() {
        mId = UUID.randomUUID();
        mTitle = "Task " + mId;
        mPriority = 1;
        mNumberOfHoursToSolve = 0;
        mIsSolved = false;
    }

    public Task(String title, int priority, int quantityHours, boolean isSolved) {
        mId = UUID.randomUUID();
        mTitle = title;
        mPriority = priority;
        mNumberOfHoursToSolve = quantityHours;
        mIsSolved = isSolved;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int mPriority) {
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
    public String toString(){
        return mTitle;
    }
}
