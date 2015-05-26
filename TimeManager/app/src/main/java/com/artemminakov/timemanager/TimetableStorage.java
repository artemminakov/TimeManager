package com.artemminakov.timemanager;


import java.util.ArrayList;
import java.util.Date;

public class TimetableStorage {
    private ArrayList<DayTimetable> mDayTimetables;


    public TimetableStorage() {
        mDayTimetables = new ArrayList<DayTimetable>();
    }

    public DayTimetable getDayTimetable(Date date) {
        for (DayTimetable d : mDayTimetables) {
            if (d.getDate().equals(date)) {
                return d;
            }
        }
        return null;
    }

    public void addDayTimetable(DayTimetable dayTimetable) {
        mDayTimetables.add(dayTimetable);
    }
}
