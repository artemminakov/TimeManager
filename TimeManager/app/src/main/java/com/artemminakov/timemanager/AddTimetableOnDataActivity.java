package com.artemminakov.timemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class AddTimetableOnDataActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.add_timetable_on_data_activity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_task:
/*
                Task task = new Task();
                TaskLab.get(this).addTask(task);
*/
                Intent i = new Intent(this.getApplicationContext(), AddTaskToDayTimetableActivity.class);
                startActivity(i);
                return true;
            /*case android.R.id.home:
                if (NavUtils.getParentActivityName(this) != null){
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
