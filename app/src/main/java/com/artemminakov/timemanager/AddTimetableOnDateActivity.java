package com.artemminakov.timemanager;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;


public class AddTimetableOnDateActivity extends Activity {

    GoogleAccountCredential mCredential;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    private String yearCalendarView = "year";
    private String monthCalendarView = "month";
    private String dayOfMonthCalendarView = "day";
    private boolean[] tasksSolve = new boolean[15];
    private StringBuilder dateTimetable = new StringBuilder();
    private StringBuilder dateTimetableTitle = new StringBuilder();
    private Date date;

    private int year;
    private int month;
    private int dayOfMonth;

    private ArrayList<Task> mTasks;
    private TaskDatabaseHelper taskDBHelper;
    private SQLiteDatabase taskDB;
    private final String LOG_TAG = "AddTimetableOnDate";
    private int taskPositionInTimetable;

    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_PRIORITY = "priority";
    private static final String COLUMN_TASK_QUANTITY_HOURS = "quantityHours";
    private static final String COLUMN_TASK_IS_SOLVED = "isSolved";
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

            TextView titleTextView = (TextView) convertView
                    .findViewById(R.id.today_task_list_item_titleTextView);
            titleTextView.setText(task.getTitle());
            CheckBox solvedCheckBox = (CheckBox) convertView
                    .findViewById(R.id.today_task_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(tasksSolve[position]);
            TextView timeTextView = (TextView) convertView
                    .findViewById(R.id.today_task_list_item_timeTextView);
            timeTextView.setText(task.getTaskTime(position));


            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        taskDBHelper = new TaskDatabaseHelper(getApplicationContext());
        taskDB = taskDBHelper.getWritableDatabase();
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate()");
        setContentView(R.layout.add_timetable_on_data_activity);
        year = getIntent().getIntExtra(yearCalendarView, 2015);
        month = getIntent().getIntExtra(monthCalendarView, 1);
        dayOfMonth = getIntent().getIntExtra(dayOfMonthCalendarView, 1);
        Log.d(LOG_TAG, "Year = " + year);
//        date = new Date(year-1900, month, dayOfMonth);
        date = new GregorianCalendar(year, month, dayOfMonth).getTime();
        if (dayOfMonth < 10) {
            dateTimetable.append(0);
        }
        dateTimetable.append(dayOfMonth).append(".");
        dateTimetableTitle.append(dateTimetable);
        if (month < 10) {
            dateTimetableTitle.append(0);
        }
        dateTimetable.append(month + 1).append(".").append(year);
        TaskDatabaseHelper.addTimeteableToDatabase(dateTimetable.toString(), taskDB);
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
                Task task = (Task) lvMain.getItemAtPosition(position);
                taskPositionInTimetable = position + 1;
                Intent intent;
                if (task.getTitle().equals(" ")) {
                    intent = new Intent(getApplicationContext(),
                            AddTaskToDayTimetableActivity.class);
                    intent.putExtra(taskExecuted, "Executed");
                    startActivityForResult(intent, 1);
                } else {
                    intent = new Intent(getApplicationContext(), EditTaskActivity.class);
                    intent.putExtra(COLUMN_TASK_TITLE, task.getTitle());
                    intent.putExtra(COLUMN_TASK_PRIORITY, task.getPriority());
                    intent.putExtra(COLUMN_TASK_QUANTITY_HOURS,
                            Integer.toString(task.getNumberOfHoursToSolve()));
                    intent.putExtra(COLUMN_TASK_IS_SOLVED, (tasksSolve[position] ? 1 : 0));
                    intent.putExtra(taskExecuted, "Executed");
                    intent.putExtra(timetableDate, dateTimetable.toString());
                    intent.putExtra(taskPosition, position);
                    startActivity(intent);
                }
            }
        });

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                this.getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sync_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                if (mTasks.size() < 15) {
                    Intent intent = new Intent(this.getApplicationContext(),
                            AddTaskToDayTimetableActivity.class);
                    startActivityForResult(intent, 0);
                    return true;
                }
                return true;
            case android.R.id.home:
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            case R.id.action_sync_task:
                GoogleCalendarApi.sendResultsToApi(this,
                        this.getApplicationContext(), mCredential, mTasks, date);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == RESULT_OK) {
                    GoogleCalendarApi.sendResultsToApi(this,
                            this.getApplicationContext(), mCredential, mTasks, date);
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);


                        GoogleCalendarApi.sendResultsToApi(this,
                                this.getApplicationContext(), mCredential, mTasks, date);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {

                    GoogleCalendarApi.sendResultsToApi(this,
                            this.getApplicationContext(), mCredential, mTasks, date);
                }
                break;
        }
        if (data == null) {
            return;
        }
        int taskResId;
        taskResId = data.getIntExtra("taskId", 1);
        Log.d(LOG_TAG, "Full activity result -> " + dateTimetable.toString() + ", " + taskResId);
        TaskDatabaseHelper.queryUpdateTask(dateTimetable.toString(), taskResId,
                taskPositionInTimetable, taskDB);
        return;
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
        tasksSolve = TaskDatabaseHelper.queryGetOnDateTimetable(dateTimetable.toString(),
                taskDB, getApplicationContext());
        ListView listView = (ListView) findViewById(R.id.listViewTasksAddTimetableOnData);
        TaskAdapter adapter = new TaskAdapter(mTasks);
        listView.setAdapter(adapter);
    }

}
