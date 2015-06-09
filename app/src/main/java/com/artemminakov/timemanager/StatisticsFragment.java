package com.artemminakov.timemanager;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
    private static final String TABLE_TIMETABLESOLVE = "timetableSolve";
    private static final String COLUMN_TIMETABLESOLVE_DATE = "date";

    private int countExecuteTask = 0;
    private int countOverdueTask = 0;

    private DateFormat df = new SimpleDateFormat("dd.M.yyyy");
    private Date currDate = new Date();
    private TaskDatabaseHelper taskDBHelper;
    Calendar cal = Calendar.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.statistics_fragment, null);

        Spinner statisticsSpinner = (Spinner) view.findViewById(R.id.spinner_StatisticsFragment);
        final TextView executionTasksCount = (TextView) view.findViewById(R.id.textViewStatisticsFragmentExecutionTasksCount);
        final TextView overdueTasksCount = (TextView) view.findViewById(R.id.textViewStatisticsFragmentOverdueTasksCount);

        statisticsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        setZeroStatistics();
                        queryTaskDBHelper(df.format(currDate));
                        break;
                    case 1:
                        cal.setTime(currDate);
                        cal.add(Calendar.DATE, -7);
                        Date dateMin = cal.getTime();
                        setZeroStatistics();
                        queryBetweenDateTaskDBHelper(df.format(dateMin), df.format(currDate));
                        break;
                    default:
                        cal.setTime(currDate);
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        Date dateMin1 = cal.getTime();
                        setZeroStatistics();
                        queryBetweenDateTaskDBHelper(df.format(dateMin1), df.format(currDate));
                        break;
                }
                executionTasksCount.setText(Integer.toString(countExecuteTask));
                overdueTasksCount.setText(Integer.toString(countOverdueTask));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setZeroStatistics();
                queryTaskDBHelper(df.format(currDate));
                executionTasksCount.setText(Integer.toString(countExecuteTask));
                overdueTasksCount.setText(Integer.toString(countOverdueTask));
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setZeroStatistics();
    }

    private void queryTaskDBHelper(String date) {
        taskDBHelper = new TaskDatabaseHelper(this.getActivity().getApplicationContext());
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();

        String sqlQuery = "select * from timetableSolve where date = \"" + date + "\"";

        Cursor c = db.rawQuery(sqlQuery, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    for (String cn : c.getColumnNames()) {
                        if (cn.matches("idTimetableSolve") || cn.matches("date")) {
                            continue;
                        } else {
                            int isSolvedColIndex = c.getColumnIndex(cn);
                            boolean isSolved = (c.getInt(isSolvedColIndex) != 0);
                            if (isSolved) {
                                countExecuteTask++;
                            } else {
                                countOverdueTask++;
                            }
                        }
                    }
                } while (c.moveToNext());
            }
        } else

            c.close();
    }

    private void queryBetweenDateTaskDBHelper(String date1, String date2) {
        taskDBHelper = new TaskDatabaseHelper(this.getActivity().getApplicationContext());
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();

        Cursor c = db.query(TABLE_TIMETABLESOLVE, null, COLUMN_TIMETABLESOLVE_DATE + " BETWEEN ? AND ?", new String[]{
                date1, date2}, null, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    for (String cn : c.getColumnNames()) {
                        if (cn.matches("idTimetableSolve") || cn.matches("date")) {
                            continue;
                        } else {
                            int isSolvedColIndex = c.getColumnIndex(cn);
                            boolean isSolved = (c.getInt(isSolvedColIndex) != 0);
                            if (isSolved) {
                                countExecuteTask++;
                            } else {
                                countOverdueTask++;
                            }
                        }
                    }
                } while (c.moveToNext());
            }
        }

            c.close();
    }

    private void setZeroStatistics(){
        countExecuteTask = 0;
        countOverdueTask = 0;
    }
}