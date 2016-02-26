package com.artemminakov.timemanager;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class AddTaskActivity extends Activity {

    private String[] priority = {"Низкий", "Средний", "Высокий"};

    private String taskPriority;

    private TaskDatabaseHelper taskDBHelper;
    private SQLiteDatabase taskDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.add_task_activity);
        Button addButton = (Button) findViewById(R.id.add_btn_task_AddTaskActivity);
        final EditText titleEditText = (EditText) findViewById(R.id.title_AddTaskActivity);
        final EditText quantityHoursEditText = (EditText) findViewById(R.id.quantH_editText_AddTaskActivity);
        Spinner prioritySpinner = (Spinner) findViewById(R.id.spinner_AddTaskActivity);
        taskDBHelper = new TaskDatabaseHelper(getApplicationContext());
        taskDB = taskDBHelper.getWritableDatabase();
        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                taskPriority = priority[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                taskPriority = priority[0];
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!titleEditText.getText().toString().matches("") &&
                        !quantityHoursEditText.getText().toString().matches("")) {
                    Intent intent = new Intent();
                    Task task = new Task();
                    task.setPriority(taskPriority);
                    task.setTitle(titleEditText.getText().toString());
                    task.setNumberOfHoursToSolve(Integer.parseInt(quantityHoursEditText.getText().toString()));
                    TaskDatabaseHelper.queryAddTaskToDatabase(task, taskDB);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
