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
import android.view.Menu;
import android.view.MenuInflater;
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
    private static final String COLUMN_TIMETABLE_TASKID1 = "taskId1";
    private static final String COLUMN_TIMETABLE_TASKID2 = "taskId2";
    private static final String COLUMN_TIMETABLE_TASKID3 = "taskId3";
    private static final String COLUMN_TIMETABLE_TASKID4 = "taskId4";
    private static final String COLUMN_TIMETABLE_TASKID5 = "taskId5";
    private static final String COLUMN_TIMETABLE_TASKID6 = "taskId6";
    private static final String COLUMN_TIMETABLE_TASKID7 = "taskId7";
    private static final String COLUMN_TIMETABLE_TASKID8 = "taskId8";
    private static final String COLUMN_TIMETABLE_TASKID9 = "taskId9";
    private static final String COLUMN_TIMETABLE_TASKID10 = "taskId10";
    private static final String COLUMN_TIMETABLE_TASKID11 = "taskId11";
    private static final String COLUMN_TIMETABLE_TASKID12 = "taskId12";
    private static final String COLUMN_TIMETABLE_TASKID13 = "taskId13";
    private static final String COLUMN_TIMETABLE_TASKID14 = "taskId14";
    private static final String COLUMN_TIMETABLE_TASKID15 = "taskId15";

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate()");
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.add_timetable_on_data_activity);
        year = getIntent().getIntExtra(yearCalendarView, 2015);
        month = getIntent().getIntExtra(monthCalendarView, 1);
        dayOfMonth = getIntent().getIntExtra(dayOfMonthCalendarView, 1);
        if(dayOfMonth<10){
            dateTimetable.append(0);
        }
        dateTimetable.append(dayOfMonth).append(".");
        dateTimetableTitle.append(dateTimetable);
        if (month<10){
            dateTimetableTitle.append(0);
        }
        dateTimetable.append(month + 1).append(".").append(year);
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
                Task task = (Task)lvMain.getItemAtPosition(position);
                i.putExtra(COLUMN_TASK_TITLE, task.getTitle());
                i.putExtra(COLUMN_TASK_PRIORITY, task.getPriority());
                i.putExtra(COLUMN_TASK_QUANTITY_HOURS, Integer.toString(task.getNumberOfHoursToSolve()));
                i.putExtra(COLUMN_TASK_IS_SOLVED, (tasksSolve[position]? 1 : 0));
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
                Intent i = new Intent(this.getApplicationContext(), AddTaskToDayTimetableActivity.class);
                startActivityForResult(i, 0);
                return true;
            case android.R.id.home:
                if (NavUtils.getParentActivityName(this) != null){
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
        int taskResId = 0;
        for (int i = 0; i < taskIds.length; i++){
            if (taskIds[i] == 0) {
                positionInTasksIds = 0;
                break;
            }
        }
        taskResId = Integer.parseInt(data.getStringExtra("taskId"));
        taskIds[positionInTasksIds] = taskResId;
        addTaskToDatabase(dateTimetable.toString());
    }

    @Override
    public void onPause(){
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
        taskDBHelper = new TaskDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();

        String sqlQuery = "select * from timetable where date = \"" + date + "\"";

        Log.d(LOG_TAG, "Date = " + date);

        Cursor c = db.rawQuery(sqlQuery, null);
        if (c != null) {
            if (c.moveToFirst()) {
                StringBuilder sb = new StringBuilder();
                do {
                    sb.setLength(0);
                    for (String cn : c.getColumnNames()) {
                        if (cn.matches("idTimetable") || cn.matches("date")) {
                            continue;
                        } else {
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
                                    DayTimetable.get(getApplicationContext()).addTask(resTask);
                                }
                            }
                            c1.close();
                        }
                    }
                } while (c.moveToNext());
            }
        }

        c.close();

        Cursor c2 = db.rawQuery("select * from timetableSolve where date = \"" + date + "\"", null);
        if (c2 != null) {
            if (c2.moveToFirst()) {
                do {
                    for (String cn : c2.getColumnNames()) {
                        if (cn.matches("idTimetableSolve") || cn.matches("date")) {
                            continue;
                        } else {
                            int isSolvedColIndex = c2.getColumnIndex(cn);
                            boolean isSolved = (c2.getInt(isSolvedColIndex) != 0);
                            if (positionInTaskSolve < tasksSolve.length) {
                                if (isSolved) {
                                    tasksSolve[positionInTaskSolve++] = true;
                                } else {
                                    tasksSolve[positionInTaskSolve++] = false;
                                }
                            }
                        }
                    }
                } while (c2.moveToNext());
            }
        }

        c2.close();
        positionInTaskSolve = 0;
    }

    private void addTaskToDatabase(String date) {
        ContentValues cvTimetable = new ContentValues();
        ContentValues cvSolve = new ContentValues();
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();
        cvTimetable.put(COLUMN_TIMETABLE_DATE, date);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID1, taskIds[0]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID2, taskIds[1]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID3, taskIds[2]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID4, taskIds[3]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID5, taskIds[4]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID6, taskIds[5]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID7, taskIds[6]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID8, taskIds[7]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID9, taskIds[8]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID10, taskIds[9]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID11, taskIds[10]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID12, taskIds[11]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID13, taskIds[12]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID14, taskIds[13]);
        cvTimetable.put(COLUMN_TIMETABLE_TASKID15, taskIds[14]);
        db.insert(TABLE_TIMETABLE, null, cvTimetable);

        cvSolve.put(COLUMN_TIMETABLE_DATE, date);
        cvSolve.put(COLUMN_TIMETABLE_TASKID1, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID2, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID3, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID4, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID5, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID6, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID7, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID8, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID9, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID10, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID11, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID12, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID13, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID14, 0);
        cvSolve.put(COLUMN_TIMETABLE_TASKID15, 0);

        db.insert(TABLE_TIMETABLESOLVE, null, cvSolve);
    }
}
