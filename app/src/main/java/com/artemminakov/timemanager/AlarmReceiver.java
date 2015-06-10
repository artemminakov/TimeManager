package com.artemminakov.timemanager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmReceiver extends BroadcastReceiver {

    private String[] taskPriorityH = new String[15];

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar now = GregorianCalendar.getInstance();
        int dayOfWeek = now.get(Calendar.DATE);
        taskPriorityH = intent.getStringArrayExtra("taskH");

        String str = new SimpleDateFormat("HH").format(Calendar.getInstance().getTime());
        if (taskPriorityH != null) {
            for (int i = 0; i < taskPriorityH.length; i++) {
                String taskTime;
                if (taskPriorityH[i] != null){
                    taskTime = taskPriorityH[i];
                }else {
                    continue;
                }
                if (str.matches(taskTime)) {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentTitle(context.getResources().getString(R.string.message_box_title))
                                    .setContentText(context.getResources().getString(R.string.message_timesheet_not_up_to_date));
                    Intent resultIntent = new Intent(context, MainActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(1, mBuilder.build());
                }
            }
        }
    }

}
