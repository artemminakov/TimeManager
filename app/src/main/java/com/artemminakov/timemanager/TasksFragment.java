package com.artemminakov.timemanager;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import java.util.ArrayList;

public class TasksFragment extends Fragment {
    private ArrayList<Task> mTasks;
    private String taskTitle;
    private String taskPriority;
    private int taskQuantityHours;

    private static final String TABLE_TASK = "tasks";
    private static final String COLUMN_TASK_ID = "idTask";
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

        if (isNotCreateTasks()){
            Task task = new Task(" ", "Средний", 5, false);
            addTaskToDatabase(task);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        queryTaskDBHelper();
        ListView listViewTasks = (ListView) this.getActivity().findViewById(R.id.listViewTasks);
        TaskAdapter taskAdapter = new TaskAdapter(mTasks);
        listViewTasks.setAdapter(taskAdapter);
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
        super.onDestroy();
        TaskLab.get(getActivity()).clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        TaskLab.get(getActivity()).clear();
    }

    private void queryTaskDBHelper() {
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
                TaskLab.get(getActivity()).addTask(resTask);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void addTaskToDatabase(Task task) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase taskDB = taskDBHelper.getWritableDatabase();
        contentValues.put(COLUMN_TASK_TITLE, task.getTitle());
        contentValues.put(COLUMN_TASK_PRIORITY, task.getPriority());
        contentValues.put(COLUMN_TASK_QUANTITY_HOURS, task.getNumberOfHoursToSolve());
        contentValues.put(COLUMN_TASK_IS_SOLVED, (task.isSolved() ? 1 : 0));
        contentValues.put(COLUMN_TASK_SPENT_ON_SOLUTION, 0);
        taskDB.insert(TABLE_TASK, null, contentValues);
    }

    private boolean isNotCreateTasks(){
        taskDBHelper = new TaskDatabaseHelper(getActivity().getApplicationContext());
        SQLiteDatabase taskDB = taskDBHelper.getWritableDatabase();
        Cursor cursor = taskDB.rawQuery("select * from tasks where idTask = \"" + "1" + "\"", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
        }

        cursor.close();
        return true;
    }
}