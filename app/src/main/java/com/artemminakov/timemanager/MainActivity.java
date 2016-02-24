package com.artemminakov.timemanager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence drawerTitle;
    private CharSequence title;
    private String[] itemTitles;

    private static TodayFragment todayFragment;
    private static CalendarFragment calendarFragment;
    private static TasksFragment tasksFragment;
    private static StatisticsFragment statisticsFragment;
    private static FragmentManager fragmentManager;
    private static final String FRAGMENT_TAG = "FRAGMENT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = drawerTitle = getTitle();
        itemTitles = getResources().getStringArray(R.array.menu_items_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        fragmentManager = getFragmentManager();
        todayFragment = new TodayFragment();
        calendarFragment = new CalendarFragment();
        tasksFragment = new TasksFragment();
        statisticsFragment = new StatisticsFragment();

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, itemTitles));

        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments

        Fragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ItemFragment.ARG_ITEM_NUMBER, position);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();


        // update selected item and title, then close the drawer

        drawerList.setItemChecked(position, true);
        setTitle(itemTitles[position]);
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getActionBar().setTitle(this.title);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class ItemFragment extends Fragment {
        public static final String ARG_ITEM_NUMBER = "item_number";

        public ItemFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int itemNumber = getArguments().getInt(ARG_ITEM_NUMBER);
            String menuItems = getResources().getStringArray(R.array.menu_items_array)[itemNumber];
            View rootView = null;
            FragmentTransaction fragmentTransaction;


            switch (itemNumber) {
                case 0:
                    fragmentTransaction = fragmentManager
                            .beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, todayFragment,
                            FRAGMENT_TAG);
                    fragmentTransaction.commit();
                    break;
                case 1:
                    fragmentTransaction = fragmentManager
                            .beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, calendarFragment,
                            FRAGMENT_TAG);
                    fragmentTransaction.commit();
                    break;
                case 2:
                    fragmentTransaction = fragmentManager
                            .beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, tasksFragment,
                            FRAGMENT_TAG);
                    fragmentTransaction.commit();
                    break;
                case 3:
                    fragmentTransaction = fragmentManager
                            .beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, statisticsFragment,
                            FRAGMENT_TAG);
                    fragmentTransaction.commit();
                    break;
                default:
                    fragmentTransaction = fragmentManager
                            .beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, todayFragment,
                            FRAGMENT_TAG);
                    fragmentTransaction.commit();
                    break;
            }


            getActivity().setTitle(menuItems);
            return rootView;
        }
    }
}