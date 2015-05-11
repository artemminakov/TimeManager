package com.artemminakov.timemanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class AddTaskActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.add_task_activity);
    }
}
