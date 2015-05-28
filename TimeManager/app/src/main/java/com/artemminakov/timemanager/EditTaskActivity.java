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
import android.widget.EditText;
import android.widget.Spinner;


public class EditTaskActivity extends Activity {

    private static final String[] priority = {"Обычный", "Средний", "Высокий", "Повышенный"};

    private static final String taskTitleName = "title";
    private static final String taskQuantityHoursName = "quantityHours";
    private static final String taskPriorityName = "priority";
    private static final String taskIsSolvedName = "isSolved";

    private static final String TABLE_TASK = "task";
    private static final String COLUMN_TASK_ID = "idTask";
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_PRIORITY = "priority";
    private static final String COLUMN_TASK_QUANTITY_HOURS = "quantityHours";
    private static final String COLUMN_TASK_IS_SOLVED = "isSolved";

    private String extraPriority;
    private int selectonPrioritySpinner = 1;
    private String taskPriority;

    private TaskDatabaseHelper taskDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.add_task_activity);
        Button editButton = (Button) findViewById(R.id.add_btn_task_AddTaskActivity);
        editButton.setText("Редактировать");
        final EditText titleEditText = (EditText) findViewById(R.id.title_AddTaskActivity);
        titleEditText.setText(getIntent().getStringExtra(taskTitleName));
        final EditText quantityHoursEditText = (EditText) findViewById(R.id.quantH_editText_AddTaskActivity);
        extraPriority = getIntent().getStringExtra(taskPriorityName);
        quantityHoursEditText.setText(getIntent().getStringExtra(taskQuantityHoursName));
        Spinner prioritySpinner = (Spinner) findViewById(R.id.spinner_AddTaskActivity);
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
                editQueryTaskDBHelper(titleEditText.getText().toString(), taskPriority, quantityHoursEditText.getText().toString());
                /*Intent intent = new Intent();
                intent.putExtra(taskPriorityName, taskPriority);
                intent.putExtra(taskTitleName, titleEditText.getText().toString());
                intent.putExtra(taskQuantityHoursName, quantityHoursEditText.getText().toString());
                intent.putExtra(taskIsSolved, )
                setResult(RESULT_OK, intent);*/
                finish();
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
        cv.put(COLUMN_TASK_IS_SOLVED, 1);

        db.update(TABLE_TASK, cv, "idTask = ?", new String[]{Integer.toString(editTaskId)});

    }
}
