package com.artemminakov.timemanager;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
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
import java.util.Calendar;
import java.util.Date;


public class TodayFragment extends Fragment {

    private ArrayList<Task> mTasks;
    private TaskDatabaseHelper taskDBHelper;
    private boolean[] tasksSolve = new boolean[15];
    private int positionInTaskSolve = 0;
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
    private static final String taskPosition = "taskPosition";
    private static final int NOTIFY_ID = 101;

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
        notificationImportantTask();
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
                i.putExtra(taskPosition, position);
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
                                    DayTimetable.get(this.getActivity().getApplicationContext()).addTask(resTask);
                                }
                            }
                            c1.close();
                        }
                    }
                } while (c.moveToNext());
            }
        } else

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
                            if (positionInTaskSolve < 15) {
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

    private void notificationImportantTask(){
        Context context = getActivity().getApplicationContext();

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);
        String str = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_drawer))
                        //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker("Важная задача!!!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle("Напоминание")
                        //.setContentText(res.getString(R.string.notifytext))
                .setContentText("Делай дело!"); // Текст уведомленимя

        // Notification notification = builder.getNotification(); // до API 16
        //if(str.matches("01:25")) {
            Notification notification = builder.getNotification();

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFY_ID, notification);
       // }
    }
}
