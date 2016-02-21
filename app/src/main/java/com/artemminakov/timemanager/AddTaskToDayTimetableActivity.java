package com.artemminakov.timemanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_PRIORITY = "priority";
    private static final String COLUMN_TASK_QUANTITY_HOURS = "quantityHours";
    private static final String COLUMN_TASK_IS_SOLVED = "isSolved";

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
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.add_task_to_day_timetable_activity);
        mTasks = TaskLab.get(this).getTasks();
        final ListView lvMain = (ListView) findViewById(R.id.listViewTasks_AddToDay);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                StringBuilder posTask = new StringBuilder();
                posTask.append(position + 1);
                intent.putExtra("taskId", posTask.toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        queryTaskDBHelper();
        ListView listView = (ListView) findViewById(R.id.listViewTasks_AddToDay);
        TaskAdapter adapter = new TaskAdapter(mTasks);
        listView.setAdapter(adapter);
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
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();

        Cursor c = db.query(TABLE_TASK, null, null, null, null, null, null);

        if (c.moveToFirst()) {

            int titleColIndex = c.getColumnIndex(COLUMN_TASK_TITLE);
            int priorityColIndex = c.getColumnIndex(COLUMN_TASK_PRIORITY);
            int quantityHColIndex = c.getColumnIndex(COLUMN_TASK_QUANTITY_HOURS);
            int isSolvedColIndex = c.getColumnIndex(COLUMN_TASK_IS_SOLVED);

            do {
                Task resTask = new Task();
                resTask.setTitle(c.getString(titleColIndex));
                resTask.setPriority(c.getString(priorityColIndex));
                resTask.setNumberOfHoursToSolve(c.getInt(quantityHColIndex));
                resTask.setIsSolved((c.getInt(isSolvedColIndex) != 0));
                TaskLab.get(this).addTask(resTask);
            } while (c.moveToNext());
        }
        c.close();
    }
}
