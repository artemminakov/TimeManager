package com.artemminakov.timemanager;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
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
    private boolean[] tasksSolve = new boolean[15];
    private int positionInTaskSolve = 0;
    private static String[] taskTime = {"08", "09", "10", "11", "12", "13", "14", "15", "16", "17",
            "18", "19", "20", "21", "22"};
    private static String[] taskTimePriorityH = new String[15];
    private static String[] taskTimePriorityHTitle = new String[15];
    private int positionInTaskTimePriorityH = 0;
    private String dateTimetable = "23.2.2016";
    private int taskPositionInTimetable = 2;
    final String LOG_TAG = "myLogs";


    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_PRIORITY = "priority";
    private static final String COLUMN_TASK_QUANTITY_HOURS = "quantityHours";
    private static final String COLUMN_TASK_IS_SOLVED = "isSolved";
    private static final String TABLE_TIMETABLE = "timetable";

    private static final String taskTitle = "title";
    private static final String taskPriority = "priority";
    private static final String taskQuantityHours = "quantityHours";
    private static final String taskIsSolved = "isSolved";
    private static final String taskExecuted = "executed";
    private static final String timetableDate = "timetableDate";
    private static final String taskPosition = "taskPosition";

    private DateFormat dateFormat = new SimpleDateFormat("dd.M.yyyy");
    private Date currentDate = new Date();

    public class TaskAdapter extends ArrayAdapter<Task> {
        public TaskAdapter(ArrayList<Task> tasks) {
            super(getActivity(), 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_today_task, null);
            }

            Task task = getItem(position);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.today_task_list_item_titleTextView);
            titleTextView.setText(task.getTitle());
            CheckBox solvedCheckBox = (CheckBox) convertView.findViewById(R.id.today_task_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(tasksSolve[position]);
            TextView timeTextView = (TextView) convertView.findViewById(R.id.today_task_list_item_timeTextView);
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
                Task task = (Task) lvMain.getItemAtPosition(position);
                taskPositionInTimetable = position + 1;
                Intent intent;
                if (task.getTitle().equals(" ")) {
                    intent = new Intent(getActivity().getApplicationContext(), AddTaskToDayTimetableActivity.class);
                    Log.d(LOG_TAG, "Empty task -> AddTaskToDayTimetable.class");
                    startActivityForResult(intent, 1);
                } else {
                    intent = new Intent(getActivity().getApplicationContext(), EditTaskActivity.class);

                    intent.putExtra(taskTitle, task.getTitle());
                    intent.putExtra(taskPriority, task.getPriority());
                    intent.putExtra(taskQuantityHours, Integer.toString(task.getNumberOfHoursToSolve()));
                    intent.putExtra(taskIsSolved, (tasksSolve[position] ? 1 : 0));
                    intent.putExtra(taskExecuted, "Executed");
                    intent.putExtra(timetableDate, dateFormat.format(currentDate));
                    intent.putExtra(taskPosition, position);
                    Log.d(LOG_TAG, "Full task -> EditTaskActivity.class");
                    startActivity(intent);
                }
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
        queryTaskDBHelper(dateFormat.format(currentDate));
        mTasks = DayTimetable.get(getActivity()).getTasks();
        ListView listView = (ListView) this.getActivity().findViewById(R.id.listViewSchedule);
        TaskAdapter taskAdapter = new TaskAdapter(mTasks);
        listView.setAdapter(taskAdapter);
        handleNotification();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            Log.d(LOG_TAG, "Empty activity result");
            return;
        }
        int taskResId;
        taskResId = Integer.parseInt(data.getStringExtra("taskId"));
        //dateTimetable = data.getStringExtra(timetableDate);
        Log.d(LOG_TAG, "Full activity result -> " + dateFormat.format(currentDate) + ", " + taskResId);
        updateTaskDB(dateFormat.format(currentDate), taskResId);
    }

    private void queryTaskDBHelper(String date) {
        taskDBHelper = new TaskDatabaseHelper(this.getActivity().getApplicationContext());
        SQLiteDatabase taskDB = taskDBHelper.getWritableDatabase();

        String sqlQuery = "select * from timetable where date = \"" + date + "\"";

        Cursor cursor = taskDB.rawQuery(sqlQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    for (String columnName : cursor.getColumnNames()) {
                        if (columnName.matches("idTimetable") || columnName.matches("date")) {
                            continue;
                        } else {
                            Cursor cursor1 = taskDB.rawQuery("select * from tasks where idTask = \"" + cursor.getString(cursor.getColumnIndex(columnName)) + "\"", null);
                            if (cursor1 != null) {
                                if (cursor1.moveToFirst()) {
                                    int titleColIndex = cursor1.getColumnIndex(COLUMN_TASK_TITLE);
                                    int priorityColIndex = cursor1.getColumnIndex(COLUMN_TASK_PRIORITY);
                                    int quantityHColIndex = cursor1.getColumnIndex(COLUMN_TASK_QUANTITY_HOURS);
                                    int isSolvedColIndex = cursor1.getColumnIndex(COLUMN_TASK_IS_SOLVED);
                                    Task resTask = new Task();
                                    String priority = cursor1.getString(priorityColIndex);
                                    int columnIndex = cursor.getColumnIndex(columnName) - 2;
                                    if (priority.matches("Высокий")) {
                                        if (columnIndex < 15 && positionInTaskTimePriorityH < 15) {
                                            taskTimePriorityH[positionInTaskTimePriorityH] =
                                                    taskTime[columnIndex];
                                            taskTimePriorityHTitle[positionInTaskTimePriorityH++] =
                                                    cursor1.getString(titleColIndex);
                                        }
                                    }
                                    resTask.setTitle(cursor1.getString(titleColIndex));
                                    resTask.setPriority(cursor1.getString(priorityColIndex));
                                    resTask.setNumberOfHoursToSolve(cursor1.getInt(quantityHColIndex));
                                    resTask.setIsSolved((cursor1.getInt(isSolvedColIndex) != 0));
                                    DayTimetable.get(this.getActivity().getApplicationContext()).addTask(resTask);
                                }
                            }
                            cursor1.close();
                        }
                    }
                } while (cursor.moveToNext());
            }
        } else

            cursor.close();

        Cursor cursor2 = taskDB.rawQuery("select * from timetableSolve where date = \"" + date + "\"", null);
        if (cursor2 != null) {
            if (cursor2.moveToFirst()) {
                do {
                    for (String columnName : cursor2.getColumnNames()) {
                        if (columnName.matches("idTimetableSolve") || columnName.matches("date")) {
                            continue;
                        } else {
                            int isSolvedColIndex = cursor2.getColumnIndex(columnName);
                            boolean isSolved = (cursor2.getInt(isSolvedColIndex) != 0);
                            if (positionInTaskSolve < 15) {
                                if (isSolved) {
                                    tasksSolve[positionInTaskSolve++] = true;
                                } else {
                                    tasksSolve[positionInTaskSolve++] = false;
                                }
                            }
                        }
                    }
                } while (cursor2.moveToNext());
            }
        }

        cursor2.close();
        positionInTaskSolve = 0;
    }

    private void handleNotification() {
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
        alarmIntent.putExtra("taskH", taskTimePriorityH);
        alarmIntent.putExtra("taskHTitle", taskTimePriorityHTitle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 3600000, pendingIntent);
    }

    private void updateTaskDB(String dateTimetable, int taskId) {
        taskDBHelper = new TaskDatabaseHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();
        ContentValues cvTimetable = new ContentValues();

        cvTimetable.put("taskId" + taskPositionInTimetable, taskId);

        db.update(TABLE_TIMETABLE, cvTimetable, "date = ?", new String[]{dateTimetable});
    }
}
