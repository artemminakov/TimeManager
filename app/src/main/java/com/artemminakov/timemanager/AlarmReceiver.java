package com.artemminakov.timemanager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmReceiver extends BroadcastReceiver {

    private String[] taskPriorityH = new String[15];
    private String[] taskPriorityHTitle = new String[15];
    private String messageTitle;

    @Override
    public void onReceive(Context context, Intent intent) {
        taskPriorityH = intent.getStringArrayExtra("taskH");
        taskPriorityHTitle = intent.getStringArrayExtra("taskHTitle");
        long[] vibrate = {100, 200, 300};
        Resources res = context.getResources();

        String str = new SimpleDateFormat("HH").format(Calendar.getInstance().getTime());
        if (taskPriorityH != null) {
            for (int i = 0; i < taskPriorityH.length; i++) {
                String taskTime;
                if (taskPriorityH[i] != null) {
                    taskTime = taskPriorityH[i];
                    if (taskPriorityHTitle[i] != null)
                        messageTitle = taskPriorityHTitle[i];
                } else {
                    continue;
                }
                if (str.matches(taskTime)) {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
                                    .setTicker(messageTitle)
                                    .setContentTitle(context.getResources().getString(R.string.message_box_title))
                                    .setContentText(messageTitle)
                                    .setAutoCancel(true).setVibrate(vibrate);
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
