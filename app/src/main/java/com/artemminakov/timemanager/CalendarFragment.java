package com.artemminakov.timemanager;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

public class CalendarFragment extends Fragment {

    private String yearCalendarView = "year";
    private String monthCalendarView = "month";
    private String dayOfMonthCalendarView = "day";
    private long date;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.calendar_fragment, null);
        final CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendarFragment_calendarView);
        date = calendarView.getDate();
        calendarView.setShowWeekNumber(false);
        calendarView.setFirstDayOfWeek(2);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView calendarView1, int year, int month, int dayOfMonth) {
                Intent i = new Intent(getActivity().getApplicationContext(), AddTimetableOnDataActivity.class);
                i.putExtra(yearCalendarView, year);
                i.putExtra(monthCalendarView, month);
                i.putExtra(dayOfMonthCalendarView, dayOfMonth);
                if (calendarView.getDate() != date) {
                    date = calendarView.getDate();
                    startActivity(i);
                }
            }
        });


        return view;
    }


}