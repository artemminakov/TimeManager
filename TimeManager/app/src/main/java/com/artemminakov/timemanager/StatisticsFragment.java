package com.artemminakov.timemanager;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class StatisticsFragment extends Fragment {
    final String LOG_TAG = "myLogs";
    private static final String COLUMN_TASK_IS_SOLVED = "isSolved";
    private int countExecuteTask = 0;
    private int countOverdueTask = 0;
    private int countCurrentTask = 0;

    private DateFormat df = new SimpleDateFormat("dd.M.yyyy");
    private Date currDate = new Date();
    private TaskDatabaseHelper taskDBHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.statistics_fragment, null);
        queryTaskDBHelper(df.format(currDate));

        TextView executionTasksCount = (TextView) view.findViewById(R.id.textViewStatisticsFragmentExecutionTasksCount);
        TextView overdueTasksCount = (TextView) view.findViewById(R.id.textViewStatisticsFragmentOverdueTasksCount);
        TextView currentTasksCount = (TextView) view.findViewById(R.id.textViewStatisticsFragmentCurrentTasksCount);

        executionTasksCount.setText(Integer.toString(countExecuteTask));
        overdueTasksCount.setText(Integer.toString(countOverdueTask));


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*countCurrentTask = 0;
        countExecuteTask = 0;
        countOverdueTask = 0;*/
    }

    private void queryTaskDBHelper(String date) {
        taskDBHelper = new TaskDatabaseHelper(this.getActivity().getApplicationContext());
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();

        String sqlQuery = "select * from timetable where date = \"" + date + "\"";

        Cursor c = db.rawQuery(sqlQuery, null);
        if (c != null) {
            if (c.moveToFirst()) {
                StringBuilder sb = new StringBuilder();
                do {
                    sb.setLength(0);
                    for (String cn : c.getColumnNames()) {
                        Cursor c1 = db.rawQuery("select * from task where idTask = \"" + c.getString(c.getColumnIndex(cn)) + "\"", null);
                        if (c1 != null) {
                            if (c1.moveToFirst()) {
                                int isSolvedColIndex = c1.getColumnIndex(COLUMN_TASK_IS_SOLVED);
                                boolean isSolved = (c1.getInt(isSolvedColIndex) != 0);
                                if(!isSolved){
                                    countOverdueTask ++;
                                }
                                if(isSolved){
                                    countExecuteTask ++;
                                }
                            }
                        }
                        c1.close();
                    }
                    Log.d(LOG_TAG, sb.toString());
                } while (c.moveToNext());
            }
        } else

        c.close();
    }
}