package com.artemminakov.timemanager;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarFragment extends Fragment {

    private String yearCalendarView = "year";
    private String monthCalendarView = "month";
    private String dayOfMonthCalendarView = "day";
    private long date;

    private Date currentDate = new Date();
    private DateFormat dateFormat = new SimpleDateFormat("dd.M.yyyy");


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
                Intent intent = new Intent(getActivity().getApplicationContext(), AddTimetableOnDateActivity.class);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(currentDate.getTime());
                if (calendarView.getDate() != date) {
                    intent.putExtra(yearCalendarView, year);
                    intent.putExtra(monthCalendarView, month);
                    intent.putExtra(dayOfMonthCalendarView, dayOfMonth);
                    date = calendarView.getDate();
                    startActivity(intent);
                }

            }
        });


        return view;
    }


}