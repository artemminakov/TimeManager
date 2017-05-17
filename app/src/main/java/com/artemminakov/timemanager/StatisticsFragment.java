package com.artemminakov.timemanager;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class StatisticsFragment extends Fragment {

    private DateFormat df = new SimpleDateFormat("dd.M.yyyy");
    private Date currDate = new Date();

    private TaskDatabaseHelper taskDBHelper;

    private Calendar calendar = Calendar.getInstance();
    private int[] statistics = new int[2];

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        taskDBHelper = TaskDatabaseHelper.getTaskDatabaseHelper();

        View view = inflater.inflate(R.layout.statistics_fragment, null);

        Spinner statisticsSpinner = (Spinner) view.findViewById(R.id.spinner_StatisticsFragment);
        final TextView executionTasksCount = (TextView)
                view.findViewById(R.id.textViewStatisticsFragmentExecutionTasksCount);
        final TextView overdueTasksCount = (TextView)
                view.findViewById(R.id.textViewStatisticsFragmentOverdueTasksCount);

        statisticsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(23);
                switch (position) {
                    case 0:
                        setZeroStatistics();
                        statistics = taskDBHelper.queryTodayStatistic(df.format(currDate));
                        break;
                    case 1:
                        calendar.setTime(currDate);
                        calendar.add(Calendar.DATE, -6);
                        Date dateMin = calendar.getTime();
                        setZeroStatistics();
                        statistics = taskDBHelper.queryStatisticBetweenDates(df.format(dateMin),
                                df.format(currDate));
                        break;
                    default:
                        calendar.setTime(currDate);
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        Date dateMin1 = calendar.getTime();
                        setZeroStatistics();
                        statistics = taskDBHelper.queryStatisticBetweenDates(df.format(dateMin1),
                                df.format(currDate));
                        break;
                }
                executionTasksCount.setText(Integer.toString(statistics[0]));
                overdueTasksCount.setText(Integer.toString(statistics[1]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setZeroStatistics();
                statistics = taskDBHelper.queryTodayStatistic(df.format(currDate));
                executionTasksCount.setText(Integer.toString(statistics[0]));
                overdueTasksCount.setText(Integer.toString(statistics[1]));
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setZeroStatistics();
        taskDBHelper.close();
    }

    private void setZeroStatistics() {
        statistics[0] = 0;
        statistics[1] = 0;
    }
}