package com.artemminakov.timemanager;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class MakeRequestToGCalendarApi extends AsyncTask<Void, Void, Void> {
    private com.google.api.services.calendar.Calendar mService = null;
    private Exception mLastError = null;

    private ArrayList<Task> tasks;

    private Date date;

    private Activity activity;

    ProgressDialog mProgress;

    static final int REQUEST_AUTHORIZATION = 1001;

    private static final String[] taskTime = {"08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};

    private final String LOG_TAG = "MakeRequestToGC";

    @RequiresApi(api = Build.VERSION_CODES.M)
    MakeRequestToGCalendarApi(GoogleAccountCredential credential,
                              ArrayList<Task> tasks, Date date,
                              Activity activity) {
        Log.d(LOG_TAG, "MakeRequestTask(GoogleAccountCredential credential)");
        this.tasks = tasks;
        this.date = date;
        this.activity = activity;
        // Initialize credentials and service object.
        mProgress = new ProgressDialog(activity);
        mProgress.setMessage("Sync with Google Calendar");
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
    }

    /**
     * Background task to call Google Calendar API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        Log.d(LOG_TAG, "doInBackground(Void... params)");
        try {
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     *
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private Void getDataFromApi() throws IOException {
        Log.d(LOG_TAG, "getDataFromApi()");

        EventAttendee[] attendees = new EventAttendee[]{
                new EventAttendee().setEmail("frozermag@gmail.com"),
        };

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        int indexInTaskTime = 0;
        for (Task task : tasks) {
            if (!task.toString().equals(" ")) {
                Event event = new Event()
                        .setSummary(task.toString());

                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");
                Log.d(LOG_TAG, dateFormat.format(date));

                DateTime startDateTime = new DateTime(dateFormat.format(date)
                        + "T" + taskTime[indexInTaskTime] + ":00:00+02:00");
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDateTime)
                        .setTimeZone("Europe/Kiev");
                event.setStart(start);

                DateTime endDateTime = new DateTime(dateFormat.format(date)
                        + "T" + taskTime[indexInTaskTime + 1] + ":00:00+02:00");
                EventDateTime end = new EventDateTime()
                        .setDateTime(endDateTime)
                        .setTimeZone("Europe/Kiev");
                event.setEnd(end);

                String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=1"};
                event.setRecurrence(Arrays.asList(recurrence));

                event.setAttendees(Arrays.asList(attendees));

                event.setReminders(reminders);

                Log.d(LOG_TAG, event.toString());

                String calendarId = "primary";
                event = mService.events().insert(calendarId, event).execute();
                Log.d(LOG_TAG, task.toString());
            }
            indexInTaskTime++;
        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPreExecute() {
        Log.d(LOG_TAG, "onPreExecute()");
        mProgress.show();
    }

    @Override
    protected void onPostExecute(Void value) {
        Log.d(LOG_TAG, "onPostExecute()");
        mProgress.hide();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCancelled() {
        Log.d(LOG_TAG, "onCancelled()");
        mProgress.hide();
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                GoogleCalendarApi.showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                .getConnectionStatusCode(), activity);
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                activity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        REQUEST_AUTHORIZATION);
            }
        }
    }

}
