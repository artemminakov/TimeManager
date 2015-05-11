package com.artemminakov.timemanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class AddTaskToDayTimetableActivity extends Activity {
    private ArrayList<Task> mTasks;

    private AddTaskToDayTimetableActivity activity = this;

    public class TaskAdapter extends ArrayAdapter<Task> {
        public TaskAdapter(ArrayList<Task> tasks){
            super(activity, 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if (convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.list_item_task, null);
            }

            Task task = getItem(position);

            TextView titleTextView = (TextView)convertView.findViewById(R.id.task_list_item_titleTextView);
            titleTextView.setText(task.getTitle());
            CheckBox solvedCheckBox = (CheckBox)convertView.findViewById(R.id.task_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(task.isSolved());

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.tasks_fragment);
        mTasks = TaskLab.get(this).getTasks();
        ListView lvMain = (ListView) findViewById(R.id.listViewTasks);
        TaskAdapter adapter = new TaskAdapter(mTasks);


        lvMain.setAdapter(adapter);
    }
}
