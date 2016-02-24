package com.artemminakov.timemanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class AddTaskToDayTimetableActivity extends Activity {

    private ArrayList<Task> mTasks;

    private static final String TABLE_TASK = "tasks";
    private static final String COLUMN_TASK_ID = "idTask";
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_PRIORITY = "priority";
    private static final String COLUMN_TASK_QUANTITY_HOURS = "quantityHours";
    private static final String COLUMN_TASK_IS_SOLVED = "isSolved";
    final String LOG_TAG = "myLogs";

    TaskDatabaseHelper taskDBHelper;


    public class TaskAdapter extends ArrayAdapter<Task> {
        public TaskAdapter(ArrayList<Task> tasks) {
            super(getApplicationContext(), 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_task, null);
            }

            Task task = getItem(position);


            TextView titleTextView = (TextView) convertView.findViewById(R.id.task_list_item_titleTextView);
            titleTextView.setText(task.getTitle());
            CheckBox solvedCheckBox = (CheckBox) convertView.findViewById(R.id.task_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(task.isSolved());

            return convertView;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.add_task_to_day_timetable_activity);
        mTasks = TaskLab.get(this).getTasks();
        final ListView listVievTasks = (ListView) findViewById(R.id.listViewTasks_AddToDay);
        Log.d(LOG_TAG, "In AddTaskToDayTimetable.class");

        listVievTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                StringBuilder taskPosition = new StringBuilder();
                taskPosition.append(position + 2);
                Log.d(LOG_TAG, "Task position " + taskPosition.toString());
                intent.putExtra("taskId", taskPosition.toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        queryTaskDBHelper();
        ListView listViewTasks = (ListView) findViewById(R.id.listViewTasks_AddToDay);
        TaskAdapter taskAdapter = new TaskAdapter(mTasks);
        listViewTasks.setAdapter(taskAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TaskLab.get(this).clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        TaskLab.get(this).clear();
    }

    private void queryTaskDBHelper() {
        taskDBHelper = new TaskDatabaseHelper(getApplicationContext());
        SQLiteDatabase taskDB = taskDBHelper.getWritableDatabase();

        Cursor cursor = taskDB.query(TABLE_TASK, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            int idTask = cursor.getColumnIndex(COLUMN_TASK_ID);
            int titleColIndex = cursor.getColumnIndex(COLUMN_TASK_TITLE);
            int priorityColIndex = cursor.getColumnIndex(COLUMN_TASK_PRIORITY);
            int quantityHColIndex = cursor.getColumnIndex(COLUMN_TASK_QUANTITY_HOURS);
            int isSolvedColIndex = cursor.getColumnIndex(COLUMN_TASK_IS_SOLVED);

            do {
                if (cursor.getString(idTask).equals("1")) {
                    continue;
                }
                Task resTask = new Task();
                resTask.setTitle(cursor.getString(titleColIndex));
                resTask.setPriority(cursor.getString(priorityColIndex));
                resTask.setNumberOfHoursToSolve(cursor.getInt(quantityHColIndex));
                resTask.setIsSolved((cursor.getInt(isSolvedColIndex) != 0));
                TaskLab.get(this).addTask(resTask);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
