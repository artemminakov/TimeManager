package com.artemminakov.timemanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class EditTaskActivity extends Activity {

    private static final String[] priority = {"Низкий", "Средний", "Высокий"};
    final String LOG_TAG = "myLogs";

    private static final String taskTitleName = "title";
    private static final String taskQuantityHoursName = "quantityHours";
    private static final String taskPriorityName = "priority";
    private static final String taskIsSolvedName = "isSolved";
    private static final String taskExecuted = "executed";
    private static final String timetableDate = "timetableDate";
    private static final String taskPosition = "taskPosition";

    private static final String TABLE_TASK = "task";
    private static final String COLUMN_TASK_ID = "idTask";
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_PRIORITY = "priority";
    private static final String COLUMN_TASK_QUANTITY_HOURS = "quantityHours";
    private static final String COLUMN_TASK_IS_SOLVED = "isSolved";
    private static final String COLUMN_TASK_SPENT_ON_SOLUTION = "spentOnSolution";

    private static final String TABLE_TIMETABLE = "timetable";
    private static final String COLUMN_TIMETABLE_DATE = "date";
    private static final String COLUMN_TIMETABLE_TASKID1 = "taskId1";
    private static final String COLUMN_TIMETABLE_TASKID2 = "taskId2";
    private static final String COLUMN_TIMETABLE_TASKID3 = "taskId3";
    private static final String COLUMN_TIMETABLE_TASKID4 = "taskId4";
    private static final String COLUMN_TIMETABLE_TASKID5 = "taskId5";
    private static final String COLUMN_TIMETABLE_TASKID6 = "taskId6";
    private static final String COLUMN_TIMETABLE_TASKID7 = "taskId7";
    private static final String COLUMN_TIMETABLE_TASKID8 = "taskId8";
    private static final String COLUMN_TIMETABLE_TASKID9 = "taskId9";
    private static final String COLUMN_TIMETABLE_TASKID10 = "taskId10";
    private static final String COLUMN_TIMETABLE_TASKID11 = "taskId11";
    private static final String COLUMN_TIMETABLE_TASKID12 = "taskId12";
    private static final String COLUMN_TIMETABLE_TASKID13 = "taskId13";
    private static final String COLUMN_TIMETABLE_TASKID14 = "taskId14";
    private static final String COLUMN_TIMETABLE_TASKID15 = "taskId15";

    private String extraPriority;
    private int selectonPrioritySpinner = 1;
    private String taskPriority;
    private boolean isSolvedTask = false;
    private String isExecutedTask;
    private String dateTimetable;
    private int taskPositionInTimetable;

    private TaskDatabaseHelper taskDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.edit_task_activity);
        Button editButton = (Button) findViewById(R.id.edit_btn_task_EditTaskActivity);
        final EditText titleEditText = (EditText) findViewById(R.id.title_EditTaskActivity);
        titleEditText.setText(getIntent().getStringExtra(taskTitleName));
        final EditText quantityHoursEditText = (EditText) findViewById(R.id.quantH_editText_EditTaskActivity);
        extraPriority = getIntent().getStringExtra(taskPriorityName);
        quantityHoursEditText.setText(getIntent().getStringExtra(taskQuantityHoursName));
        final CheckBox checkBoxSolve = (CheckBox) findViewById(R.id.checkBox_EditTaskActivity);
        checkBoxSolve.setChecked((getIntent().getIntExtra(taskIsSolvedName, 1) != 0));
        isExecutedTask = getIntent().getStringExtra(taskExecuted);
        TextView checkBoxSolveTitle = (TextView) findViewById(R.id.checkboxTitle_textView_EditTaskActivity);
        if (isExecutedTask != null){
            editButton.setText("Выполнить!");
        }
        else {
            checkBoxSolve.setVisibility(View.VISIBLE);
            checkBoxSolveTitle.setVisibility(View.VISIBLE);
        }
        dateTimetable = getIntent().getStringExtra(timetableDate);
        Spinner prioritySpinner = (Spinner) findViewById(R.id.spinner_EditTaskActivity);
        for (int i = 0; i < priority.length; i++) {
            if (priority[i].equals(extraPriority)) {
                selectonPrioritySpinner = i;
                break;
            }
        }
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
                if (dateTimetable != null){
                    taskPositionInTimetable = getIntent().getIntExtra(taskPosition, 1);
                    editSolveQueryTaskDBHelper(dateTimetable, taskPositionInTimetable);
                    finish();
                }
                else{
                    isSolvedTask = checkBoxSolve.isChecked();
                    editQueryTaskDBHelper(titleEditText.getText().toString(), taskPriority, quantityHoursEditText.getText().toString());
                    finish();
                }
            }
        });
    }

    private void editQueryTaskDBHelper(String titleTask, String priorityTask, String quantityHoursTask) {
        taskDBHelper = new TaskDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String editTaskTitle = getIntent().getStringExtra(taskTitleName);
        int editTaskId = 1;

        Cursor c = db.query(TABLE_TASK, null, null, null, null, null, null);

        if (c.moveToFirst()) {

            int idColIndex = c.getColumnIndex(COLUMN_TASK_ID);
            int titleColIndex = c.getColumnIndex(COLUMN_TASK_TITLE);

            do {
                String edit = (c.getString(titleColIndex));
                if (editTaskTitle.equals(edit)) {
                    editTaskId = c.getInt(idColIndex);
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();

        cv.put(COLUMN_TASK_TITLE, titleTask);
        cv.put(COLUMN_TASK_PRIORITY, priorityTask);
        cv.put(COLUMN_TASK_QUANTITY_HOURS, Integer.parseInt(quantityHoursTask));
        cv.put(COLUMN_TASK_IS_SOLVED, (isSolvedTask ? 1 : 0));

        db.update(TABLE_TASK, cv, "idTask = ?", new String[]{Integer.toString(editTaskId)});

    }

    private void editSolveQueryTaskDBHelper(String dateTimetable, int taskPosition) {
        taskDBHelper = new TaskDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String editTaskTitle = getIntent().getStringExtra(taskTitleName);
        int editTaskId = 1;
        int spentOnSolution = 0;

        Cursor c = db.query(TABLE_TASK, null, null, null, null, null, null);
        Log.d(LOG_TAG, "--- editSolveQueryTaskDBHelper ---");

        if (c.moveToFirst()) {

            int idColIndex = c.getColumnIndex(COLUMN_TASK_ID);
            int titleColIndex = c.getColumnIndex(COLUMN_TASK_TITLE);

            do {
                String edit = (c.getString(titleColIndex));
                if (editTaskTitle.equals(edit)) {
                    editTaskId = c.getInt(idColIndex);
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();

        Cursor c1 = db.rawQuery("select * from task where idTask = \"" + editTaskId + "\"", null);
        if (c1 != null) {
            if (c1.moveToFirst()) {
                /*int titleColIndex = c1.getColumnIndex(COLUMN_TASK_TITLE);
                Log.d(LOG_TAG, "--- Task title ---" + c1.getString(titleColIndex));
                int priorityColIndex = c1.getColumnIndex(COLUMN_TASK_PRIORITY);
                Log.d(LOG_TAG, "--- Task priority ---" + c1.getString(priorityColIndex));
                int quantityHColIndex = c1.getColumnIndex(COLUMN_TASK_QUANTITY_HOURS);
                Log.d(LOG_TAG, "--- Task quantity hours ---" + c1.getInt(quantityHColIndex));
                int isSolvedColIndex = c1.getColumnIndex(COLUMN_TASK_IS_SOLVED);
                Log.d(LOG_TAG, "--- Task is solved ---" + c1.getInt(isSolvedColIndex));*/
                int toSolveHours = c1.getColumnIndex(COLUMN_TASK_SPENT_ON_SOLUTION);
                spentOnSolution = c1.getInt(toSolveHours) + 1;
            }
        }
        c1.close();

        cv.put(COLUMN_TASK_SPENT_ON_SOLUTION, spentOnSolution);

        db.update(TABLE_TASK, cv, "idTask = ?", new String[]{Integer.toString(editTaskId)});

    }

}
