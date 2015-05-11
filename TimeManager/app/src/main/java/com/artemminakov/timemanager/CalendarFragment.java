package com.artemminakov.timemanager;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;


public class CalendarFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.calendar_fragment, null);
        CalendarView calendarView = (CalendarView)view.findViewById(R.id.calendarFragment_calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView calendView, int year, int month, int dayOfMonth) {
                Intent i = new Intent(getActivity().getApplicationContext(), AddTimetableOnDataActivity.class);
                startActivity(i);
            }
        });

        calendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(), AddTimetableOnDataActivity.class);
                startActivity(i);
            }
        });

        return view;
    }
}
