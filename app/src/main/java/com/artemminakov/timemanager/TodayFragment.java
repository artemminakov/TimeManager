package com.artemminakov.timemanager;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TodayFragment extends Fragment {

    private ArrayList<Task> mTasks;
    private TaskDatabaseHelper taskDBHelper;
    private boolean[] tasksSolve;
    final String LOG_TAG = "myLogs";

    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_PRIORITY = "priority";
    private static final String COLUMN_TASK_QUANTITY_HOURS = "quantityHours";
    private static final String COLUMN_TASK_IS_SOLVED = "isSolved";

    private static final String taskTitle = "title";
    private static final String taskPriority = "priority";
    private static final String taskQuantityHours = "quantityHours";
    private static final String taskIsSolved = "isSolved";
    private static final String taskExecuted = "executed";
    private static final String timetableDate = "timetableDate";

    private DateFormat df = new SimpleDateFormat("dd.M.yyyy");
    private Date currDate = new Date();

    public class TaskAdapter extends ArrayAdapter<Task>{
        public TaskAdapter(ArrayList<Task> tasks){
            super(getActivity(), 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if (convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_today_task, null);
            }

            Task task = getItem(position);

            TextView titleTextView = (TextView)convertView.findViewById(R.id.today_task_list_item_titleTextView);
            titleTextView.setText(task.getTitle());
            CheckBox solvedCheckBox = (CheckBox)convertView.findViewById(R.id.today_task_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(tasksSolve[position]);
            TextView timeTextView = (TextView)convertView.findViewById(R.id.today_task_list_item_timeTextView);
            timeTextView.setText(task.getTaskTime(position));


            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.today_fragment, null);
        final ListView lvMain = (ListView) view.findViewById(R.id.listViewSchedule);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity().getApplicationContext(), EditTaskActivity.class);
                Task task = (Task)lvMain.getItemAtPosition(position);
                i.putExtra(taskTitle, task.getTitle());
                i.putExtra(taskPriority, task.getPriority());
                i.putExtra(taskQuantityHours, Integer.toString(task.getNumberOfHoursToSolve()));
                i.putExtra(taskIsSolved, (tasksSolve[position]? 1 : 0));
                i.putExtra(taskExecuted, "Executed");
                i.putExtra(timetableDate, df.format(currDate));
                startActivity(i);
            }
        });

        return view;
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        DayTimetable.get(getActivity()).clear();
    }


    @Override
    public void onPause() {
        super.onPause();
        DayTimetable.get(getActivity()).clear();
    }


    @Override
    public void onResume() {
        super.onResume();
        queryTaskDBHelper(df.format(currDate));
        mTasks = DayTimetable.get(getActivity()).getTasks();
        tasksSolve = DayTimetable.get(getActivity()).getSolvedTasks();
        ListView listView = (ListView) this.getActivity().findViewById(R.id.listViewSchedule);
        TaskAdapter adapter = new TaskAdapter(mTasks);
        listView.setAdapter(adapter);
    }


    private void queryTaskDBHelper(String date) {
        taskDBHelper = new TaskDatabaseHelper(this.getActivity().getApplicationContext());
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();

        String sqlQuery = "select * from timetable where date = \"" + date + "\"";
        Log.d(LOG_TAG, "--- Insert in mytable: ---" + date);


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
                                int titleColIndex = c1.getColumnIndex(COLUMN_TASK_TITLE);
                                int priorityColIndex = c1.getColumnIndex(COLUMN_TASK_PRIORITY);
                                int quantityHColIndex = c1.getColumnIndex(COLUMN_TASK_QUANTITY_HOURS);
                                int isSolvedColIndex = c1.getColumnIndex(COLUMN_TASK_IS_SOLVED);
                                Task resTask = new Task();
                                resTask.setTitle(c1.getString(titleColIndex));
                                resTask.setPriority(c1.getString(priorityColIndex));
                                resTask.setNumberOfHoursToSolve(c1.getInt(quantityHColIndex));
                                resTask.setIsSolved((c1.getInt(isSolvedColIndex) != 0));
                                DayTimetable.get(this.getActivity().getApplicationContext()).addTask(resTask);
                            }
                        }
                        c1.close();
                    }
                } while (c.moveToNext());
            }
        } else

        c.close();
    }

}
