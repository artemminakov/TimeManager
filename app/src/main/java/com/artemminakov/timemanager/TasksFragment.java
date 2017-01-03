package com.artemminakov.timemanager;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TasksFragment extends Fragment {
    private ArrayList<Task> mTasks;

    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_PRIORITY = "priority";
    private static final String COLUMN_TASK_QUANTITY_HOURS = "quantityHours";
    private static final String COLUMN_TASK_IS_SOLVED = "isSolved";
    private static final String LOG_TAG = "TasksFragment";

    private TaskDatabaseHelper taskDBHelper;
    private SQLiteDatabase taskDB;


    public class TaskAdapter extends ArrayAdapter<Task> {
        public TaskAdapter(ArrayList<Task> tasks) {
            super(getActivity(), 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_task, null);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.task_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                Intent intent = new Intent(getActivity().getApplicationContext(), AddTaskActivity.class);
                startActivityForResult(intent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tasks_fragment, null);
        mTasks = TaskLab.get(getActivity()).getTasks();
        final ListView listViewTasks = (ListView) view.findViewById(R.id.listViewTasks);
        registerForContextMenu(listViewTasks);
        setHasOptionsMenu(true);

        taskDBHelper = new TaskDatabaseHelper(getActivity().getApplicationContext());
        taskDB = taskDBHelper.getWritableDatabase();
        Log.d(LOG_TAG, "onCreateView!");

        if (TaskDatabaseHelper.queryIsNotCreateTasks(taskDB)) {
            Task task = new Task(" ", "Средний", 5, false);
            TaskDatabaseHelper.queryAddTaskToDatabase(task, taskDB);
        }

        listViewTasks.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), EditTaskActivity.class);
                Task task = (Task) listViewTasks.getItemAtPosition(position);
                intent.putExtra(COLUMN_TASK_TITLE, task.getTitle());
                intent.putExtra(COLUMN_TASK_PRIORITY, task.getPriority());
                intent.putExtra(COLUMN_TASK_QUANTITY_HOURS, Integer.toString(task.getNumberOfHoursToSolve()));
                intent.putExtra(COLUMN_TASK_IS_SOLVED, (task.isSolved() ? 1 : 0));
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate!");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume!");
        TaskDatabaseHelper.queryGetTasks(taskDB, getActivity());
        ListView listViewTasks = (ListView) this.getActivity().findViewById(R.id.listViewTasks);
        Collections.reverse(mTasks);
        TaskAdapter taskAdapter = new TaskAdapter(mTasks);
        listViewTasks.setAdapter(taskAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        Log.d(LOG_TAG, "onActivityResult!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy!");
        TaskLab.get(getActivity()).clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause!");
        TaskLab.get(getActivity()).clear();
    }

}