package com.artemminakov.timemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.Collections;


public class AddTaskToDayTimetableActivity extends Activity {

    private ArrayList<Task> mTasks;

    private final String LOG_TAG = "ATTDTA";

    private TaskDatabaseHelper taskDBHelper;


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


            TextView titleTextView = (TextView) convertView
                    .findViewById(R.id.task_list_item_titleTextView);
            titleTextView.setText(task.getTitle());
            CheckBox solvedCheckBox = (CheckBox) convertView
                    .findViewById(R.id.task_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(task.isSolved());

            return convertView;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                Intent intent = new Intent(getApplicationContext(), AddTaskActivity.class);
                startActivityForResult(intent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.add_task_to_day_timetable_activity);
        mTasks = TaskLab.get().getTasks();
        final ListView listVievTasks = (ListView) findViewById(R.id.listViewTasks_AddToDay);
        Log.d(LOG_TAG, "In AddTaskToDayTimetable.class");
        taskDBHelper = TaskDatabaseHelper.getTaskDatabaseHelper();

        listVievTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = (Task) listVievTasks.getItemAtPosition(position);
                Intent intent = new Intent();
                StringBuilder taskPosition = new StringBuilder();
                taskPosition.append(position + 2);
                Log.d(LOG_TAG, "Task position " + taskPosition.toString());
                intent.putExtra("taskId", taskDBHelper.queryGetTaskId(task.getTitle()));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        taskDBHelper.queryGetTasks();
        ListView listViewTasks = (ListView) findViewById(R.id.listViewTasks_AddToDay);
        Collections.reverse(mTasks);
        TaskAdapter taskAdapter = new TaskAdapter(mTasks);
        listViewTasks.setAdapter(taskAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        TaskLab.get().clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TaskLab.get().clear();
        taskDBHelper.close();
    }

}
