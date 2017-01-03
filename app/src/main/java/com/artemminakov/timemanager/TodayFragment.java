package com.artemminakov.timemanager;

import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

public class TodayFragment extends Fragment {

    GoogleAccountCredential mCredential;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    private ArrayList<Task> mTasks = new ArrayList<>(15);
    private TaskDatabaseHelper taskDBHelper;
    private SQLiteDatabase taskDB;
    private boolean[] tasksSolve = new boolean[15];
    private int positionInTaskSolve = 0;
    private static final String[] taskTime = {"08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    private static String[] taskTimePriorityH = new String[15];
    private static String[] taskTimePriorityHTitle = new String[15];
    private int positionInTaskTimePriorityH = 0;
    private int taskPositionInTimetable = 2;
    private static final String LOG_TAG = "TodayFragment";


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
    private static final String taskPosition = "taskPosition";

    private final String NOTIFICATIONEXTR = "Notification";
    private String isNotification = "";

    ProgressDialog mProgress;

    private DateFormat dateFormat = new SimpleDateFormat("dd.M.yyyy");
    private Date currentDate = new Date();

    public class TaskAdapter extends ArrayAdapter<Task> {
        public TaskAdapter(ArrayList<Task> tasks) {
            super(getActivity(), 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_today_task, null);
            }

            Task task = getItem(position);

            TextView titleTextView = (TextView) convertView.
                    findViewById(R.id.today_task_list_item_titleTextView);
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Log.d(LOG_TAG, "onCreate!");
        taskDBHelper = new TaskDatabaseHelper(getActivity().getApplicationContext());
        taskDB = taskDBHelper.getWritableDatabase();
        TaskDatabaseHelper.addTimeteableToDatabase(dateFormat.format(currentDate), taskDB);
        queryTaskDBHelper(dateFormat.format(currentDate));
        if (TaskDatabaseHelper.queryIsNotCreateTasks(taskDB)) {
            Task task = new Task(" ", "Средний", 5, false);
            TaskDatabaseHelper.queryAddTaskToDatabase(task, taskDB);
        }
        isNotification = getActivity().getIntent().getStringExtra(NOTIFICATIONEXTR);

        Log.d(LOG_TAG, "onCreate! " + isNotification);

        if (isNotification == null) {
            handleNotification();
        }

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                this.getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.sync_menu, menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync_task:
                GoogleCalendarApi.sendResultsToApi(this.getActivity(),
                        this.getContext(), mCredential, mTasks, currentDate);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.today_fragment, null);
        final ListView lvMain = (ListView) view.findViewById(R.id.listViewSchedule);
        Log.d(LOG_TAG, "onCreateView!");

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = (Task) lvMain.getItemAtPosition(position);
                taskPositionInTimetable = position + 1;
                Intent intent;
                if (task.getTitle().equals(" ")) {
                    intent = new Intent(getActivity().getApplicationContext(),
                            AddTaskToDayTimetableActivity.class);
                    Log.d(LOG_TAG, "Empty task -> AddTaskToDayTimetable.class");
                    startActivityForResult(intent, 1);
                } else {
                    intent = new Intent(getActivity().getApplicationContext(),
                            EditTaskActivity.class);

                    intent.putExtra(taskTitle, task.getTitle());
                    intent.putExtra(taskPriority, task.getPriority());
                    intent.putExtra(taskQuantityHours,
                            Integer.toString(task.getNumberOfHoursToSolve()));
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
        Log.d(LOG_TAG, "onDestroy!");
        DayTimetable.get(getActivity()).clear();
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause!");
        DayTimetable.get(getActivity()).clear();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    //FIX this
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume!");
        TaskDatabaseHelper.addTimeteableToDatabase(dateFormat.format(currentDate), taskDB);
        queryTaskDBHelper(dateFormat.format(currentDate));
        mTasks = DayTimetable.get(getActivity()).getTasks();
        mProgress = new ProgressDialog(this.getActivity());
        mProgress.setMessage("Sync with cist.nure");
        new NureAsyncTask().execute();
        int counter = 0;
        for (Task t : mTasks) {
            Log.d(LOG_TAG, t.toString() + " " + counter++);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == RESULT_OK) {
                    GoogleCalendarApi.sendResultsToApi(this.getActivity(),
                            this.getContext(), mCredential, mTasks, currentDate);
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                this.getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);

                        GoogleCalendarApi.sendResultsToApi(this.getActivity(),
                                this.getContext(), mCredential, mTasks, currentDate);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {

                    GoogleCalendarApi.sendResultsToApi(this.getActivity(),
                            this.getContext(), mCredential, mTasks, currentDate);
                }
                break;
        }
        if (data == null) {
            Log.d(LOG_TAG, "Empty activity result");
            return;
        }
        int taskResId;
        taskResId = data.getIntExtra("taskId", 1);
        Log.d(LOG_TAG, "Full activity result -> " + dateFormat
                .format(currentDate) + ", " + taskResId);
        TaskDatabaseHelper.queryUpdateTask(dateFormat
                .format(currentDate), taskResId, taskPositionInTimetable, taskDB);
    }

    private void queryTaskDBHelper(String date) {

        String sqlQuery = "select * from timetable where date = \"" + date + "\"";
        Log.d(LOG_TAG, "queryTaskDBHelper!");

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

        DateFormat writeFormat = new SimpleDateFormat("HH");
        int hours = Integer.parseInt(writeFormat.format(currentDate));

        if (hours > 6) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, ++hours);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            Log.d(LOG_TAG, "Hours! " + hours);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) this.getActivity().getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600000, pendingIntent);
        }
    }


    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(LOG_TAG, "onRequestPermissionsResult(int requestCode,\n" +
                " @NonNull String[] permissions,\n" +
                " @NonNull int[] grantResults)");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    class NureAsyncTask extends AsyncTask<Void, Void, Void> {


        private final String LOG_TAG = "NureAsyncTask";

        private final int[] taskTime = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                22, 23};

        private ArrayList<String> tasks = new ArrayList<>();

        @RequiresApi(api = Build.VERSION_CODES.M)
        NureAsyncTask() {
            Log.d(LOG_TAG, "MakeRequestTask(GoogleAccountCredential credential)");

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(LOG_TAG, "doInBackground(Void... params)");
            try {
                return getDataFromApi();
            } catch (Exception e) {
                cancel(true);
                return null;
            }
        }

        private Void getDataFromApi() throws IOException {
            Log.d(LOG_TAG, "getDataFromApi()");
            JSONObject json = null;
            Log.d(LOG_TAG, "before");
            for (Task t : mTasks) {
                Log.d(LOG_TAG, t.toString());
            }
            Log.d(LOG_TAG, "after\n\n\n\n\n\n\n\n");
            try {
                json = readJsonFromUrl("http://cist.nure.ua/ias/app/tt/P_API_EVENTS_GROUP_JSON?" +
                        "p_id_group=4307198&time_from=1483101900&time_to=1484563900");

                JSONArray arr = json.getJSONArray("events");

                for (int i = 0; i < arr.length(); i++) {
                    int subject_id = arr.getJSONObject(i).getInt("subject_id");
                    Calendar curCal = Calendar.getInstance();
                    curCal.setTime(currentDate);

                    Date start_time = new Date(arr.getJSONObject(i).getInt("start_time") * 1000L);
                    Calendar startCal = Calendar.getInstance();
                    startCal.setTime(start_time);
                    Date end_time = new Date(arr.getJSONObject(i).getInt("end_time") * 1000L);
                    Calendar endCal = Calendar.getInstance();
                    endCal.setTime(end_time);
                    if (startCal.get(Calendar.MONTH) == curCal.get(Calendar.MONTH)
                            && startCal.get(Calendar.DAY_OF_MONTH) == curCal.get(Calendar.DAY_OF_MONTH)) {
//                        Log.d(LOG_TAG, "" + curCal.get(Calendar.MONTH) + " " + curCal.get(Calendar.DAY_OF_MONTH) +
//                                " " + startCal.get(Calendar.MONTH) + " " + startCal.get(Calendar.DAY_OF_MONTH));
                        for (int j = 0; j < taskTime.length; j++) {
                            if (taskTime[j] >= startCal.get(Calendar.HOUR)
                                    && taskTime[j] <= endCal.get(Calendar.HOUR)) {
                                mTasks.set(j, new Task("Пара", "Высокий", 1, false));
                                Log.d(LOG_TAG, "" + subject_id + " " + currentDate + " " + start_time + " " + end_time);
                                continue;
                            } else {
                                /*mTasks.add(new Task(" ", "J,sxysq", 1, false));*/
                            }
                        }
                    }
                    int type = arr.getJSONObject(i).getInt("type");
                    int number_pair = arr.getJSONObject(i).getInt("number_pair");
                    String auditory = arr.getJSONObject(i).getString("auditory");
//            String teachers = arr.getJSONObject(i).getString("teachers");
                    /*Log.d(LOG_TAG, subject_id + " " + start_time + " " +
                            end_time + " " + type + " " + number_pair + " " + auditory);*/
                /*System.out.println(subject_id + " " + start_time + " " + end_time + " " + type + " " + number_pair + " "
                        + auditory + " " *//*+ teachers*//*);*/
                }
                int counter = 0;
                for (Task t : mTasks) {
                    Log.d(LOG_TAG, t.toString() + " " + counter++);
                }
                //System.out.println(json.toString());
                Timestamp stamp = new Timestamp(System.currentTimeMillis());
                Date date = new Date(1483508700000L);
//        Date date = new Date(stamp.getTime());
//            System.out.println(date);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPreExecute() {
            mProgress.show();
            Log.d(LOG_TAG, "onPreExecute()");
        }

        @Override
        protected void onPostExecute(Void value) {
            Log.d(LOG_TAG, "onPostExecute()");
            ListView listView = (ListView) TodayFragment.this.getActivity()
                    .findViewById(R.id.listViewSchedule);
            TaskAdapter taskAdapter = new TaskAdapter(mTasks);
            listView.setAdapter(taskAdapter);
            mProgress.hide();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onCancelled() {
            Log.d(LOG_TAG, "onCancelled()");
            mProgress.hide();

        }

        public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is,
                        Charset.forName("windows-1251")));
                String jsonText = readAll(rd);
//            String jsonText = object;
                //System.out.println(jsonText);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }

        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

    }

}
