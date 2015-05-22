package com.artemminakov.timemanager;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TasksFragment extends Fragment{
    private ArrayList<Task> mTasks;
    private String taskTitle;
    private String taskPriority;
    private int taskQuantityHours;

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

        View view = inflater.inflate(R.layout.tasks_fragment, null);
        mTasks = TaskLab.get(getActivity()).getTasks();
        final ListView lvMain = (ListView) view.findViewById(R.id.listViewTasks);
        TaskAdapter adapter = new TaskAdapter(mTasks);
        setHasOptionsMenu(true);

        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = (Task)lvMain.getItemAtPosition(position);
                Toast.makeText(getActivity().getApplicationContext(), task.getTitle() + " was clicked", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getActivity().getApplicationContext(), AddTaskActivity.class);
                startActivity(i);
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
        ListView listView = (ListView) this.getActivity().findViewById(R.id.listViewTasks);
        TaskAdapter adapter = new TaskAdapter(mTasks);
        listView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        taskTitle = data.getStringExtra("title");
        taskQuantityHours = Integer.parseInt(data.getStringExtra("quantity"));
        taskPriority = data.getStringExtra("priority");
        Task task = new Task(taskTitle, taskPriority, taskQuantityHours, false);
        TaskLab.get(getActivity()).addTask(task);
    }


}
