package com.artemminakov.timemanager;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
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
    private Calendar cal = Calendar.getInstance();
    private int[] statistics = new int[2];

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        taskDBHelper = new TaskDatabaseHelper(this.getActivity().getApplicationContext());
        final SQLiteDatabase taskDB = taskDBHelper.getWritableDatabase();

        View view = inflater.inflate(R.layout.statistics_fragment, null);

        Spinner statisticsSpinner = (Spinner) view.findViewById(R.id.spinner_StatisticsFragment);
        final TextView executionTasksCount = (TextView) view.findViewById(R.id.textViewStatisticsFragmentExecutionTasksCount);
        final TextView overdueTasksCount = (TextView) view.findViewById(R.id.textViewStatisticsFragmentOverdueTasksCount);

        statisticsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView)parent.getChildAt(0)).setTextSize(23);
                switch (position) {
                    case 0:
                        setZeroStatistics();
                        statistics = TaskDatabaseHelper.queryStatisticToday(df.format(currDate), taskDB);
                        break;
                    case 1:
                        cal.setTime(currDate);
                        cal.add(Calendar.DATE, -6);
                        Date dateMin = cal.getTime();
                        setZeroStatistics();
                        statistics = TaskDatabaseHelper.queryBetweenDateStatistic(df.format(dateMin), df.format(currDate), taskDB);
                        break;
                    default:
                        cal.setTime(currDate);
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        Date dateMin1 = cal.getTime();
                        setZeroStatistics();
                        statistics = TaskDatabaseHelper.queryBetweenDateStatistic(df.format(dateMin1), df.format(currDate), taskDB);
                        break;
                }
                executionTasksCount.setText(Integer.toString(statistics[0]));
                overdueTasksCount.setText(Integer.toString(statistics[1]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setZeroStatistics();
                statistics = TaskDatabaseHelper.queryStatisticToday(df.format(currDate), taskDB);
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
    }

    private void setZeroStatistics() {
        statistics[0] = 0;
        statistics[1] = 0;
    }
}