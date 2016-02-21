package com.artemminakov.timemanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class AddTimetableOnDataActivity extends Activity {

    private String yearCalendarView = "year";
    private String monthCalendarView = "month";
    private String dayOfMonthCalendarView = "day";
    private int[] taskIds = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int positionInTasksIds = 0;
    private boolean[] tasksSolve = new boolean[15];
    private int positionInTaskSolve = 0;
    private StringBuilder dateTimetable = new StringBuilder();
    private StringBuilder dateTimetableTitle = new StringBuilder();

    private int year;
    private int month;
    private int dayOfMonth;

    private ArrayList<Task> mTasks;
    TaskDatabaseHelper taskDBHelper;
    final String LOG_TAG = "myLogs";


    private static final String TABLE_TIMETABLE = "timetable";
    private static final String COLUMN_TIMETABLE_DATE = "date";
    private static final String[] COLUMN_TIMETABLE_TASKS = {"taskId1", "taskId2", "taskId3", "taskId4", "taskId5", "taskId6", "taskId7",
                                             "taskId8", "taskId9", "taskId10", "taskId11", "taskId12", "taskId13", "taskId14", "taskId15"};
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_PRIORITY = "priority";
    private static final String COLUMN_TASK_QUANTITY_HOURS = "quantityHours";
    private static final String COLUMN_TASK_IS_SOLVED = "isSolved";
    private static final String TABLE_TIMETABLESOLVE = "timetableSolve";
    private static final String taskExecuted = "executed";
    private static final String timetableDate = "timetableDate";
    private static final String taskPosition = "taskPosition";


    public class TaskAdapter extends ArrayAdapter<Task> {
        Context mContext;

        public TaskAdapter(ArrayList<Task> tasks) {
            super(getApplicationContext(), 0, tasks);
            this.mContext = getApplicationContext();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_today_task, null);
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
    protected void onCreate(Bundle savedInstanceState) {
        taskDBHelper = new TaskDatabaseHelper(getApplicationContext());
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate()");
        setContentView(R.layout.add_timetable_on_data_activity);
        year = getIntent().getIntExtra(yearCalendarView, 2015);
        month = getIntent().getIntExtra(monthCalendarView, 1);
        dayOfMonth = getIntent().getIntExtra(dayOfMonthCalendarView, 1);
        if (dayOfMonth < 10) {
            dateTimetable.append(0);
        }
        dateTimetable.append(dayOfMonth).append(".");
        dateTimetableTitle.append(dateTimetable);
        if (month < 10) {
            dateTimetableTitle.append(0);
        }
        dateTimetable.append(month + 1).append(".").append(year);
        addTaskToDatabase(dateTimetable.toString());
        dateTimetableTitle.append(month + 1).append(".").append(year);
        TextView titleTextView = (TextView) findViewById(R.id.textViewHeaderAddTimetableOnData);
        StringBuilder title = new StringBuilder();
        title.append("Расписание на ").append(dateTimetableTitle);
        titleTextView.setText(title);

        final ListView lvMain = (ListView) findViewById(R.id.listViewTasksAddTimetableOnData);
        mTasks = DayTimetable.get(this).getTasks();

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), EditTaskActivity.class);
                Task task = (Task) lvMain.getItemAtPosition(position);
                i.putExtra(COLUMN_TASK_TITLE, task.getTitle());
                i.putExtra(COLUMN_TASK_PRIORITY, task.getPriority());
                i.putExtra(COLUMN_TASK_QUANTITY_HOURS, Integer.toString(task.getNumberOfHoursToSolve()));
                i.putExtra(COLUMN_TASK_IS_SOLVED, (tasksSolve[position] ? 1 : 0));
                i.putExtra(taskExecuted, "Executed");
                i.putExtra(timetableDate, dateTimetable.toString());
                i.putExtra(taskPosition, position);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                if (mTasks.size() < 15) {
                    Intent i = new Intent(this.getApplicationContext(), AddTaskToDayTimetableActivity.class);
                    startActivityForResult(i, 0);
                    return true;
                }
                return true;
            case android.R.id.home:
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        int taskResId;
        taskResId = Integer.parseInt(data.getStringExtra("taskId"));
        taskIds[positionInTasksIds] = taskResId;
    }

    @Override
    public void onPause() {
        super.onPause();
        DayTimetable.get(getApplicationContext()).clear();
        Log.d(LOG_TAG, "onPause()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy()");
        DayTimetable.get(getApplicationContext()).clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume()");
        queryTaskDBHelper(dateTimetable.toString());
        ListView listView = (ListView) findViewById(R.id.listViewTasksAddTimetableOnData);
        TaskAdapter adapter = new TaskAdapter(mTasks);
        listView.setAdapter(adapter);
    }

    private void queryTaskDBHelper(String date) {
        SQLiteDatabase taskDB = taskDBHelper.getWritableDatabase();

        String sqlQuery = "select * from timetable where date = \"" + date + "\"";

        Log.d(LOG_TAG, "Date = " + date);

        Cursor cursor = taskDB.rawQuery(sqlQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    for (String cn : cursor.getColumnNames()) {
                        if (cn.matches("idTimetable") || cn.matches("date")) {
                            continue;
                        } else {
                            Cursor cursor1 = taskDB.rawQuery("select * from tasks where idTask = \"" + cursor.getString(cursor.getColumnIndex(cn)) + "\"", null);
                            if (cursor1 != null) {
                                if (cursor1.moveToFirst()) {
                                    int titleColIndex = cursor1.getColumnIndex(COLUMN_TASK_TITLE);
                                    int priorityColIndex = cursor1.getColumnIndex(COLUMN_TASK_PRIORITY);
                                    int quantityHColIndex = cursor1.getColumnIndex(COLUMN_TASK_QUANTITY_HOURS);
                                    int isSolvedColIndex = cursor1.getColumnIndex(COLUMN_TASK_IS_SOLVED);
                                    Task resTask = new Task();
                                    resTask.setTitle(cursor1.getString(titleColIndex));
                                    resTask.setPriority(cursor1.getString(priorityColIndex));
                                    resTask.setNumberOfHoursToSolve(cursor1.getInt(quantityHColIndex));
                                    resTask.setIsSolved((cursor1.getInt(isSolvedColIndex) != 0));
                                    DayTimetable.get(getApplicationContext()).addTask(resTask);
                                }
                            }
                            cursor1.close();
                        }
                    }
                } while (cursor.moveToNext());
            }
        }

        cursor.close();

        Cursor cursor2 = taskDB.rawQuery("select * from timetableSolve where date = \"" + date + "\"", null);
        if (cursor2 != null) {
            if (cursor2.moveToFirst()) {
                do {
                    for (String cn : cursor2.getColumnNames()) {
                        if (cn.matches("idTimetableSolve") || cn.matches("date")) {
                            continue;
                        } else {
                            int isSolvedColIndex = cursor2.getColumnIndex(cn);
                            boolean isSolved = (cursor2.getInt(isSolvedColIndex) != 0);
                            if (positionInTaskSolve < tasksSolve.length) {
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

    private void addTaskToDatabase(String date) {
        ContentValues cvTimetable = new ContentValues();
        ContentValues cvSolve = new ContentValues();
        SQLiteDatabase taskDB = taskDBHelper.getWritableDatabase();

        if (isNotCreateTableTimetable(date)) {
            cvTimetable.put(COLUMN_TIMETABLE_DATE, date);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[0], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[1], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[2], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[3], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[4], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[5], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[6], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[7], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[8], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[9], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[10], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[11], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[12], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[13], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[14], 1);

            taskDB.insert(TABLE_TIMETABLE, null, cvTimetable);
        }

        if (isNotCreateTableSolve(date)) {
            cvSolve.put(COLUMN_TIMETABLE_DATE, date);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[0], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[1], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[2], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[3], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[4], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[5], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[6], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[7], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[8], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[9], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[10], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[11], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[12], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[13], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[14], 0);

            taskDB.insert(TABLE_TIMETABLESOLVE, null, cvSolve);
        }
    }

    private boolean isNotCreateTableSolve(String date) {
        SQLiteDatabase taskDB = taskDBHelper.getWritableDatabase();
        Cursor cursor = taskDB.rawQuery("select * from timetableSolve where date = \"" + date + "\"", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
        }

        cursor.close();
        return true;
    }

    private boolean isNotCreateTableTimetable(String date){
        SQLiteDatabase taskDB = taskDBHelper.getWritableDatabase();
        Cursor cursor = taskDB.rawQuery("select * from timetable where date = \"" + date + "\"", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
        }

        cursor.close();
        return true;
    }
}
