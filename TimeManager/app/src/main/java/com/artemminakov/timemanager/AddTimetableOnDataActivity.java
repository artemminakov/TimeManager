package com.artemminakov.timemanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class AddTimetableOnDataActivity extends Activity {

    private String yearCalendarView = "year";
    private String monthCalendarView = "month";
    private String dayOfMonthCalendarView = "day";
    private int[] taskIds = {1, 3, 4, 6, 5, 7, 2, 1, 4, 5, 3, 7, 6, 4, 5};
    private StringBuilder dateTimetable = new StringBuilder();

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
            solvedCheckBox.setChecked(task.isSolved());
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
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.add_timetable_on_data_activity);
        year = getIntent().getIntExtra(yearCalendarView, 2015);
        month = getIntent().getIntExtra(monthCalendarView, 1);
        dayOfMonth = getIntent().getIntExtra(dayOfMonthCalendarView, 1);
        dateTimetable.append(dayOfMonth).append(".").append(month + 1).append(".").append(year);
        queryTaskDBHelper(dateTimetable.toString());
        TextView titleTextView = (TextView) findViewById(R.id.textViewHeaderAddTimetableOnData);
        StringBuilder title = new StringBuilder();
        title.append("Расписание на ").append(dayOfMonth).append(".").append(month + 1).append(".").append(year);
        titleTextView.setText(title);

        ListView lvMain = (ListView) findViewById(R.id.listViewTasksAddTimetableOnData);
        mTasks = DayTimetable.get(this).getTasks();

        TaskAdapter adapter = new TaskAdapter(mTasks);

        lvMain.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                Intent i = new Intent(this.getApplicationContext(), AddTaskToDayTimetableActivity.class);
                addTaskToDatabase(dateTimetable);
                startActivity(i);
                return true;
            /*case android.R.id.home:
                if (NavUtils.getParentActivityName(this) != null){
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DayTimetable.get(getApplicationContext()).clear();
    }

    private void queryTaskDBHelper(String date) {
        taskDBHelper = new TaskDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();

        String sqlQuery = "select * from timetable where date = \"" + date + "\"";

        Cursor c = db.rawQuery(sqlQuery, null);
        Log.d(LOG_TAG, ". Cursor is null before");
        if (c != null) {
            Log.d(LOG_TAG, ". Cursor is null after");
            if (c.moveToFirst()) {
                Log.d(LOG_TAG, ". " + c.getCount() + " rows");
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
                                DayTimetable.get(getApplicationContext()).addTask(resTask);
                            }
                        }
                        c1.close();
                        sb.append(cn + " = "
                                + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, sb.toString());
                } while (c.moveToNext());
            }
        } else
            Log.d(LOG_TAG, ". Cursor is null");

        c.close();
    }

    private void addTaskToDatabase(StringBuilder date) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();
        String d = date.toString();
        cv.put(COLUMN_TIMETABLE_DATE, d);
        cv.put(COLUMN_TIMETABLE_TASKID1, taskIds[0]);
        cv.put(COLUMN_TIMETABLE_TASKID2, taskIds[1]);
        cv.put(COLUMN_TIMETABLE_TASKID3, taskIds[2]);
        cv.put(COLUMN_TIMETABLE_TASKID4, taskIds[3]);
        cv.put(COLUMN_TIMETABLE_TASKID5, taskIds[4]);
        cv.put(COLUMN_TIMETABLE_TASKID6, taskIds[5]);
        cv.put(COLUMN_TIMETABLE_TASKID7, taskIds[6]);
        cv.put(COLUMN_TIMETABLE_TASKID8, taskIds[7]);
        cv.put(COLUMN_TIMETABLE_TASKID9, taskIds[8]);
        cv.put(COLUMN_TIMETABLE_TASKID10, taskIds[9]);
        cv.put(COLUMN_TIMETABLE_TASKID11, taskIds[10]);
        cv.put(COLUMN_TIMETABLE_TASKID12, taskIds[11]);
        cv.put(COLUMN_TIMETABLE_TASKID13, taskIds[12]);
        cv.put(COLUMN_TIMETABLE_TASKID14, taskIds[13]);
        cv.put(COLUMN_TIMETABLE_TASKID15, taskIds[14]);
        db.insert(TABLE_TIMETABLE, null, cv);
    }
}
