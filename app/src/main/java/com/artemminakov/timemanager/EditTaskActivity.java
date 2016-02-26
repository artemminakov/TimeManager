package com.artemminakov.timemanager;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class EditTaskActivity extends Activity {

    private static final String[] priority = {"Низкий", "Средний", "Высокий"};

    private static final String taskTitleName = "title";
    private static final String taskQuantityHoursName = "quantityHours";
    private static final String taskPriorityName = "priority";
    private static final String taskIsSolvedName = "isSolved";
    private static final String taskExecuted = "executed";
    private static final String timetableDate = "timetableDate";
    private static final String taskPosition = "taskPosition";

    private String extraPriority;
    private int selectonPrioritySpinner = 1;
    private String taskPriority;
    private boolean isSolvedTask = false;
    private String isExecutedTask;
    private String dateTimetable;
    private int taskPositionInTimetable;

    private TaskDatabaseHelper taskDBHelper;
    private SQLiteDatabase tasksDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskDBHelper = new TaskDatabaseHelper(getApplicationContext());
        tasksDB = taskDBHelper.getWritableDatabase();
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.edit_task_activity);

        Button editButton = (Button) findViewById(R.id.edit_btn_task_EditTaskActivity);

        final EditText titleEditText = (EditText) findViewById(R.id.title_EditTaskActivity);
        titleEditText.setText(getIntent().getStringExtra(taskTitleName));

        final EditText quantityHoursEditText = (EditText) findViewById(R.id.quantH_editText_EditTaskActivity);
        extraPriority = getIntent().getStringExtra(taskPriorityName);

        taskPositionInTimetable = getIntent().getIntExtra(taskPosition, 1) + 1;

        quantityHoursEditText.setText(getIntent().getStringExtra(taskQuantityHoursName));

        final CheckBox checkBoxSolve = (CheckBox) findViewById(R.id.checkBox_EditTaskActivity);
        checkBoxSolve.setChecked((getIntent().getIntExtra(taskIsSolvedName, 1) != 0));

        isExecutedTask = getIntent().getStringExtra(taskExecuted);

        TextView checkBoxSolveTitle = (TextView) findViewById(R.id.checkboxTitle_textView_EditTaskActivity);

        Spinner prioritySpinner = (Spinner) findViewById(R.id.spinner_EditTaskActivity);

        TextView priorityTV = (TextView) findViewById(R.id.priority_textView_EditTaskActivity);

        dateTimetable = getIntent().getStringExtra(timetableDate);
        for (int i = 0; i < priority.length; i++) {
            if (priority[i].equals(extraPriority)) {
                selectonPrioritySpinner = i;
                break;
            }
        }

        Button changeButton = (Button) findViewById(R.id.change_task_EditTaskActivity);
        if (isExecutedTask != null) {
            editButton.setText("Выполнить!");
            changeButton.setVisibility(View.VISIBLE);
            priorityTV.setText("Приоритет - " + priority[selectonPrioritySpinner]);

        } else {
            checkBoxSolve.setVisibility(View.VISIBLE);
            checkBoxSolveTitle.setVisibility(View.VISIBLE);
            titleEditText.setFocusableInTouchMode(true);
            quantityHoursEditText.setFocusableInTouchMode(true);
            prioritySpinner.setVisibility(View.VISIBLE);
        }

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTaskToDayTimetableActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        prioritySpinner.setSelection(selectonPrioritySpinner);
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

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateTimetable != null) {
                    String editTaskTitle = getIntent().getStringExtra(taskTitleName);
                    TaskDatabaseHelper.queryEditSolveTask(dateTimetable, editTaskTitle, taskPositionInTimetable, tasksDB);
                    finish();
                } else {
                    isSolvedTask = checkBoxSolve.isChecked();
                    String editTaskTitle = getIntent().getStringExtra(taskTitleName);
                    Task task = new Task();
                    task.setIsSolved(isSolvedTask);
                    task.setTitle(titleEditText.getText().toString());
                    task.setPriority(taskPriority);
                    task.setNumberOfHoursToSolve(Integer.parseInt(quantityHoursEditText.getText().toString()));
                    TaskDatabaseHelper.queryEditTask(task, editTaskTitle, tasksDB);
                    finish();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        int taskResId;
        taskResId = data.getIntExtra("taskId", 1);
        dateTimetable = getIntent().getStringExtra(timetableDate);
        taskPositionInTimetable = getIntent().getIntExtra(taskPosition, 1) + 1;
        TaskDatabaseHelper.queryUpdateTask(dateTimetable, taskResId, taskPositionInTimetable, tasksDB);
        finish();
    }

}
