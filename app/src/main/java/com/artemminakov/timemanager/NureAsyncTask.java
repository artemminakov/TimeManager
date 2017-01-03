/*
package com.artemminakov.timemanager;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class NureAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {


    private final String LOG_TAG = "NureAsyncTask";

    private static final int[] taskTime = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
            22, 23};

    private ArrayList<String> tasks = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    NureAsyncTask() {
        Log.d(LOG_TAG, "MakeRequestTask(GoogleAccountCredential credential)");

    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        Log.d(LOG_TAG, "doInBackground(Void... params)");
        try {
            return getDataFromApi();
        } catch (Exception e) {
            cancel(true);
            return null;
        }
    }

    private ArrayList<String> getDataFromApi() throws IOException {
        Log.d(LOG_TAG, "getDataFromApi()");
        JSONObject json = null;
        try {
            json = readJsonFromUrl("http://cist.nure.ua/ias/app/tt/P_API_EVENTS_GROUP_JSON?" +
                    "p_id_group=4307198&time_from=1483101900&time_to=1484563900");

            JSONArray arr = json.getJSONArray("events");

            for (int i = 0; i < arr.length(); i++) {
                int subject_id = arr.getJSONObject(i).getInt("subject_id");
                Date start_time = new Date(arr.getJSONObject(i).getInt("start_time") * 1000L);
                Date end_time = new Date(arr.getJSONObject(i).getInt("end_time") * 1000L);
                for (int j = 0;j < taskTime.length; j++){
                    if (taskTime[j] == start_time.getHours() || taskTime[j] == end_time.getHours()){
                        tasks.add("Пара");
                    } else{
                        tasks.add(" ");
                    }
                }
                int type = arr.getJSONObject(i).getInt("type");
                int number_pair = arr.getJSONObject(i).getInt("number_pair");
                String auditory = arr.getJSONObject(i).getString("auditory");
//            String teachers = arr.getJSONObject(i).getString("teachers");
                Log.d(LOG_TAG, subject_id + " " + start_time + " " +
                        end_time + " " + type + " " + number_pair + " " + auditory);
                */
/*System.out.println(subject_id + " " + start_time + " " + end_time + " " + type + " " + number_pair + " "
                        + auditory + " " *//*
*/
/*+ teachers*//*
*/
/*);*//*

            }
            //System.out.println(json.toString());
            Timestamp stamp = new Timestamp(System.currentTimeMillis());
            Date date = new Date(1483508700000L);
//        Date date = new Date(stamp.getTime());
//            System.out.println(date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPreExecute() {
        Log.d(LOG_TAG, "onPreExecute()");
    }

    protected void onPostExecute(Void value) {
        Log.d(LOG_TAG, "onPostExecute()");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCancelled() {
        Log.d(LOG_TAG, "onCancelled()");

    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is,
                    Charset.forName("windows-1251")));
            String jsonText = readAll(rd);
//            String jsonText = object;
            //System.out.println(jsonText);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}
*/
