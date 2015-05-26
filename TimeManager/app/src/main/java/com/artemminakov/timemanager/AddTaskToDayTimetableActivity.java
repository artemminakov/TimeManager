package com.artemminakov.timemanager;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;



public class AddTaskToDayTimetableActivity extends Activity {

    private static FragmentManager myFragmentManager;
    private static FragmentTransaction fragmentTransaction;
    private static TasksFragment tasksFragment = new TasksFragment();

    private static final String TAG_1 = "FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.add_task_to_day_timetable_activity);
        myFragmentManager = getFragmentManager();
        fragmentTransaction = myFragmentManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.content_frame_add_timetable, tasksFragment,
                TAG_1);
        fragmentTransaction.commit();
    }
}
