package com.artemminakov.timemanager;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class TasksFragment extends Fragment {
    private ArrayList<Task> mTasks;
    private String taskTitle;
    private String taskPriority;
    private int taskQuantityHours;
    final String LOG_TAG = "myLogs";

    private static final String TABLE_TASK = "task";
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_PRIORITY = "priority";
    private static final String COLUMN_TASK_QUANTITY_HOURS = "quantityHours";
    private static final String COLUMN_TASK_IS_SOLVED = "isSolved";
    private static final String COLUMN_TASK_SPENT_ON_SOLUTION = "spentOnSolution";

    TaskDatabaseHelper taskDBHelper;


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
                Intent i = new Intent(getActivity().getApplicationContext(), AddTaskActivity.class);
                startActivityForResult(i, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOG_TAG, "TasksFragment:onCreateView");
        View view = inflater.inflate(R.layout.tasks_fragment, null);
        mTasks = TaskLab.get(getActivity()).getTasks();
        final ListView lvMain = (ListView) view.findViewById(R.id.listViewTasks);
        setHasOptionsMenu(true);


        lvMain.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity().getApplicationContext(), EditTaskActivity.class);
                Task task = (Task)lvMain.getItemAtPosition(position);
                i.putExtra(COLUMN_TASK_TITLE, task.getTitle());
                i.putExtra(COLUMN_TASK_PRIORITY, task.getPriority());
                i.putExtra(COLUMN_TASK_QUANTITY_HOURS, Integer.toString(task.getNumberOfHoursToSolve()));
                i.putExtra(COLUMN_TASK_IS_SOLVED, (task.isSolved()? 1 : 0));
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "TasksFragment:onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "TasksFragment:onResume");
        super.onResume();
        queryTaskDBHelper();
        ListView listView = (ListView) this.getActivity().findViewById(R.id.listViewTasks);
        TaskAdapter adapter = new TaskAdapter(mTasks);
        listView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        taskTitle = data.getStringExtra("title");
        taskQuantityHours = Integer.parseInt(data.getStringExtra("quantity"));
        taskPriority = data.getStringExtra("priority");
        Task task = new Task(taskTitle, taskPriority, taskQuantityHours, false);
        addTaskToDatabase(task);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "TasksFragment:onDestroy");
        super.onDestroy();
        TaskLab.get(getActivity()).clear();
    }

    @Override
    public void onPause(){
        Log.d(LOG_TAG, "TasksFragment:onPause");
        super.onPause();
        TaskLab.get(getActivity()).clear();
    }

    private void queryTaskDBHelper() {
        taskDBHelper = new TaskDatabaseHelper(getActivity().getApplicationContext());
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
                TaskLab.get(getActivity()).addTask(resTask);
            } while (c.moveToNext());
        }
        c.close();
    }

    private void addTaskToDatabase(Task task) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();
        cv.put(COLUMN_TASK_TITLE, task.getTitle());
        cv.put(COLUMN_TASK_PRIORITY, task.getPriority());
        cv.put(COLUMN_TASK_QUANTITY_HOURS, task.getNumberOfHoursToSolve());
        cv.put(COLUMN_TASK_IS_SOLVED, (task.isSolved() ? 1 : 0));
        cv.put(COLUMN_TASK_SPENT_ON_SOLUTION, 0);
        db.insert(TABLE_TASK, null, cv);
    }

}