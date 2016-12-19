package com.artemminakov.timemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "timtmanager.sqlite";
    private static final int VERSION = 1;
    private static final String LOG_TAG = "TaskDatabaseHelper";

    private static int positionInTaskSolve = 0;

    //table tasks
    private static final String TABLE_TASK = "tasks";
    private static final String COLUMN_TASK_ID = "idTask";
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_PRIORITY = "priority";
    private static final String COLUMN_TASK_QUANTITY_HOURS = "quantityHours";
    private static final String COLUMN_TASK_IS_SOLVED = "isSolved";
    private static final String COLUMN_TASK_SPENT_ON_SOLUTION = "spentOnSolution";

    //table timetable
    private static final String TABLE_TIMETABLE = "timetable";

    //table timetableSolve
    private static final String TABLE_TIMETABLESOLVE = "timetableSolve";
    private static final String COLUMN_TIMETABLESOLVE_DATE = "date";

    private static final String COLUMN_TIMETABLE_DATE = "date";
    private static final String[] COLUMN_TIMETABLE_TASKS = {"taskId1",
            "taskId2", "taskId3", "taskId4", "taskId5", "taskId6",
            "taskId7", "taskId8", "taskId9", "taskId10", "taskId11",
            "taskId12", "taskId13", "taskId14", "taskId15"};

    private static final DateFormat format = new SimpleDateFormat("dd.M.yyyy");


    public TaskDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table tasks (idTask integer primary key autoincrement, title text, priority text, " +
                "quantityHours integer, isSolved integer, spentOnSolution integer);");

        db.execSQL("create table timetable (idTimetable integer primary key autoincrement, date text, taskId1 integer, " +
                "taskId2 integer, taskId3 integer, taskId4 integer, taskId5 integer, taskId6 integer, taskId7 integer, " +
                "taskId8 integer, taskId9 integer, taskId10 integer, taskId11 integer, taskId12 integer, taskId13 integer, " +
                "taskId14 integer, taskId15 integer);");

        db.execSQL("create table timetableSolve (idTimetableSolve integer primary key autoincrement, date text, taskId1 integer, " +
                "taskId2 integer, taskId3 integer, taskId4 integer, taskId5 integer, taskId6 integer, taskId7 integer, " +
                "taskId8 integer, taskId9 integer, taskId10 integer, taskId11 integer, taskId12 integer, taskId13 integer, " +
                "taskId14 integer, taskId15 integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static boolean queryIsNotCreateTasks(SQLiteDatabase taskDB){
        Cursor cursor = taskDB.rawQuery("select * from tasks where idTask = \"" + "1" + "\"", null);
        Log.d(LOG_TAG, "isNoCreateTasks!");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
        }

        cursor.close();
        return true;
    }

    public static void queryAddTaskToDatabase(Task task, SQLiteDatabase taskDB) {
        Log.d(LOG_TAG, "addTaskToDatabase!");
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TASK_TITLE, task.getTitle());
        contentValues.put(COLUMN_TASK_PRIORITY, task.getPriority());
        contentValues.put(COLUMN_TASK_QUANTITY_HOURS, task.getNumberOfHoursToSolve());
        contentValues.put(COLUMN_TASK_IS_SOLVED, (task.isSolved() ? 1 : 0));
        contentValues.put(COLUMN_TASK_SPENT_ON_SOLUTION, 0);
        taskDB.insert(TABLE_TASK, null, contentValues);
    }

    public static void queryGetTasks(SQLiteDatabase taskDB, Context context) {

        Cursor cursor = taskDB.query(TABLE_TASK, null, null, null, null, null, null);

        Log.d(LOG_TAG, "queryTaskDBHelper!");

        if (cursor.moveToFirst()) {

            int idTask = cursor.getColumnIndex(COLUMN_TASK_ID);
            int titleColIndex = cursor.getColumnIndex(COLUMN_TASK_TITLE);
            int priorityColIndex = cursor.getColumnIndex(COLUMN_TASK_PRIORITY);
            int quantityHColIndex = cursor.getColumnIndex(COLUMN_TASK_QUANTITY_HOURS);
            int isSolvedColIndex = cursor.getColumnIndex(COLUMN_TASK_IS_SOLVED);
            int spendOnSolveColIndex = cursor.getColumnIndex(COLUMN_TASK_SPENT_ON_SOLUTION);

            do {
                if (cursor.getString(idTask).equals("1")) {
                    continue;
                }
                Task resTask = new Task();
                resTask.setTitle(cursor.getString(titleColIndex));
                resTask.setPriority(cursor.getString(priorityColIndex));
                resTask.setNumberOfHoursToSolve(cursor.getInt(quantityHColIndex));
                int toSolve = cursor.getInt(quantityHColIndex);
                int spendOnSolve = cursor.getInt(spendOnSolveColIndex);

                if (spendOnSolve >= toSolve){
                    resTask.setIsSolved(true);
                    queryEditTask(resTask, resTask.getTitle(), taskDB);
                }else {
                    resTask.setIsSolved((cursor.getInt(isSolvedColIndex) != 0));
                }
                TaskLab.get(context).addTask(resTask);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public static int queryTaskId(String taskTitle, SQLiteDatabase taskDB) {

        Cursor cursor = taskDB.query(TABLE_TASK, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            int idTask = cursor.getColumnIndex(COLUMN_TASK_ID);
            int titleColIndex = cursor.getColumnIndex(COLUMN_TASK_TITLE);


            do {
                if (cursor.getString(titleColIndex).equals(taskTitle)) {
                    int taskPosition = cursor.getInt(idTask);
                    cursor.close();
                    return taskPosition;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return 1;
    }

    public static boolean[] queryGetOnDateTimetable(String date, SQLiteDatabase taskDB, Context context) {

        String sqlQuery = "select * from timetable where date = \"" + date + "\"";
        boolean[] tasksSolve = new boolean[15];

        Log.d(LOG_TAG, "Date = " + date);
        Log.d(LOG_TAG, "In queryTaskDBHelper!!! " );

        Cursor cursor = taskDB.rawQuery(sqlQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    for (String columnNames : cursor.getColumnNames()) {
                        if (columnNames.matches("idTimetable") || columnNames.matches("date")) {
                            continue;
                        } else {
                            Cursor cursor1 = taskDB.rawQuery("select * from tasks where idTask = \"" + cursor.getString(cursor.getColumnIndex(columnNames)) + "\"", null);
                            if (cursor1 != null) {
                                if (cursor1.moveToFirst()) {
                                    int titleColIndex = cursor1.getColumnIndex(COLUMN_TASK_TITLE);
                                    int priorityColIndex = cursor1.getColumnIndex(COLUMN_TASK_PRIORITY);
                                    int quantityHColIndex = cursor1.getColumnIndex(COLUMN_TASK_QUANTITY_HOURS);
                                    int isSolvedColIndex = cursor1.getColumnIndex(COLUMN_TASK_IS_SOLVED);
                                    Task resTask = new Task();
                                    resTask.setTitle(cursor1.getString(titleColIndex));
                                    resTask.setPriority(cursor1.getString(priorityColIndex));
                                    resTask.setNumberOfHoursToSolve(cursor1.getInt(quantityHColIndex));
                                    resTask.setIsSolved((cursor1.getInt(isSolvedColIndex) != 0));
                                    DayTimetable.get(context).addTask(resTask);
                                }
                            }
                            cursor1.close();
                        }
                    }
                } while (cursor.moveToNext());
            }
        }

        cursor.close();

        Cursor cursor2 = taskDB.rawQuery("select * from timetableSolve where date = \"" + date + "\"", null);
        if (cursor2 != null) {
            if (cursor2.moveToFirst()) {
                do {
                    for (String columnNames : cursor2.getColumnNames()) {
                        if (columnNames.matches("idTimetableSolve") || columnNames.matches("date")) {
                            continue;
                        } else {
                            int isSolvedColIndex = cursor2.getColumnIndex(columnNames);
                            boolean isSolved = (cursor2.getInt(isSolvedColIndex) != 0);
                            if (positionInTaskSolve < tasksSolve.length) {
                                if (isSolved) {
                                    tasksSolve[positionInTaskSolve++] = true;
                                } else {
                                    tasksSolve[positionInTaskSolve++] = false;
                                }
                            }
                        }
                    }
                } while (cursor2.moveToNext());
            }
        }
        cursor2.close();
        positionInTaskSolve = 0;
        return tasksSolve;
    }

    public static void queryEditTask(Task task, String editTaskTitle, SQLiteDatabase taskDB) {
        ContentValues taskCV = new ContentValues();

        int editTaskId = 1;

        Cursor cursor = taskDB.query(TABLE_TASK, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            int idColIndex = cursor.getColumnIndex(COLUMN_TASK_ID);
            int titleColIndex = cursor.getColumnIndex(COLUMN_TASK_TITLE);

            do {
                String edit = (cursor.getString(titleColIndex));
                if (editTaskTitle.equals(edit)) {
                    editTaskId = cursor.getInt(idColIndex);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        taskCV.put(COLUMN_TASK_TITLE, task.getTitle());
        taskCV.put(COLUMN_TASK_PRIORITY, task.getPriority());
        taskCV.put(COLUMN_TASK_QUANTITY_HOURS, task.getNumberOfHoursToSolve());
        taskCV.put(COLUMN_TASK_IS_SOLVED, (task.isSolved() ? 1 : 0));

        Log.d(LOG_TAG, "queryEditTask " + task.getTitle());

        taskDB.update(TABLE_TASK, taskCV, "idTask = ?", new String[]{Integer.toString(editTaskId)});

    }

    public static void queryEditSolveTask(String dateTimetable,
                                          String editTaskTitle,
                                          int taskPositionInTimetable,
                                          SQLiteDatabase taskDB,
                                        int solve) {

        ContentValues taskCV = new ContentValues();
        ContentValues timetableCV = new ContentValues();

        int editTaskId = 1;
        int spentOnSolution = 0;
        int toSolveHours;

        Cursor cursor = taskDB.query(TABLE_TASK, null, null, null, null, null, null);

        Log.d(LOG_TAG, "queryEditSolveTask ");

        if (cursor.moveToFirst()) {

            int idColIndex = cursor.getColumnIndex(COLUMN_TASK_ID);
            int titleColIndex = cursor.getColumnIndex(COLUMN_TASK_TITLE);

            do {
                String edit = (cursor.getString(titleColIndex));
                if (editTaskTitle.equals(edit)) {
                    editTaskId = cursor.getInt(idColIndex);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        Cursor cursor1 = taskDB.rawQuery("select * from tasks where idTask = \"" + editTaskId + "\"", null);
        if (cursor1 != null) {
            if (cursor1.moveToFirst() && solve == 1) {
                toSolveHours = cursor1.getColumnIndex(COLUMN_TASK_SPENT_ON_SOLUTION);
                spentOnSolution = cursor1.getInt(toSolveHours) + 1;
            }
            if (solve == 0) {
                toSolveHours = cursor1.getColumnIndex(COLUMN_TASK_SPENT_ON_SOLUTION);
                spentOnSolution = cursor1.getInt(toSolveHours) - 1;
            }
        }
        cursor1.close();

        taskCV.put(COLUMN_TASK_SPENT_ON_SOLUTION, spentOnSolution);

        taskDB.update(TABLE_TASK, taskCV, "idTask = ?", new String[]{Integer.toString(editTaskId)});

        timetableCV.put("taskId" + taskPositionInTimetable, solve);

        taskDB.update(TABLE_TIMETABLESOLVE, timetableCV, "date = ?", new String[]{dateTimetable});

    }

    public static void queryUpdateTask(String dateTimetable, int taskId, int taskPositionInTimetable, SQLiteDatabase taskDB) {
        ContentValues timetableCV = new ContentValues();

        Log.d(LOG_TAG, "queryEditSolveTask ");

        timetableCV.put("taskId" + taskPositionInTimetable, taskId);

        taskDB.update(TABLE_TIMETABLE, timetableCV, "date = ?", new String[]{dateTimetable});

    }

    public static void addTimeteableToDatabase(String date, SQLiteDatabase taskDB) {
        ContentValues cvTimetable = new ContentValues();
        ContentValues cvSolve = new ContentValues();


        if (isNotCreateTableTimetable(date, taskDB)) {
            cvTimetable.put(COLUMN_TIMETABLE_DATE, date);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[0], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[1], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[2], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[3], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[4], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[5], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[6], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[7], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[8], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[9], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[10], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[11], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[12], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[13], 1);
            cvTimetable.put(COLUMN_TIMETABLE_TASKS[14], 1);

            taskDB.insert(TABLE_TIMETABLE, null, cvTimetable);
        }

        if (isNotCreateTableSolve(date, taskDB)) {
            cvSolve.put(COLUMN_TIMETABLE_DATE, date);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[0], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[1], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[2], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[3], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[4], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[5], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[6], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[7], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[8], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[9], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[10], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[11], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[12], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[13], 0);
            cvSolve.put(COLUMN_TIMETABLE_TASKS[14], 0);

            taskDB.insert(TABLE_TIMETABLESOLVE, null, cvSolve);
        }
    }

    private static boolean isNotCreateTableSolve(String date, SQLiteDatabase taskDB) {
        Cursor cursor = taskDB.rawQuery("select * from timetableSolve where date = \"" + date + "\"", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
        }

        cursor.close();
        return true;
    }

    private static boolean isNotCreateTableTimetable(String date, SQLiteDatabase taskDB) {
        Cursor cursor = taskDB.rawQuery("select * from timetable where date = \"" + date + "\"", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
        }

        cursor.close();
        return true;
    }

    public static int[] queryStatisticToday(String date, SQLiteDatabase taskDB) {

        String sqlQuery = "select * from timetableSolve where date = \"" + date + "\"";

        int statistic[] = new int[2];
        int countExecuteTask = 0;
        int countOverdueTask = 0;

        Cursor cursor = taskDB.rawQuery(sqlQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    for (String cn : cursor.getColumnNames()) {
                        if (cn.matches("idTimetableSolve") || cn.matches("date")) {
                            continue;
                        } else {
                            int isSolvedColIndex = cursor.getColumnIndex(cn);
                            boolean isSolved = (cursor.getInt(isSolvedColIndex) != 0);
                            if (isSolved) {
                                countExecuteTask++;
                            } else {
                                countOverdueTask++;
                            }
                        }
                    }
                } while (cursor.moveToNext());
            }
        }
        cursor.close();

        String sqlQuerySec = "select * from timetable where date = \"" + date + "\"";

        Cursor cursor1 = taskDB.rawQuery(sqlQuerySec, null);
        if (cursor1 != null) {
            if (cursor1.moveToFirst()) {
                do {
                    for (String columnNames : cursor1.getColumnNames()) {
                        if (columnNames.matches("idTimetable") || columnNames.matches("date")) {
                            continue;
                        } else {
                            int taskId = Integer.parseInt(cursor1.getString(cursor1.getColumnIndex(columnNames)));
                            if (taskId == 1) {
                                countOverdueTask--;
                            }
                        }
                    }
                } while (cursor1.moveToNext());
            }
        }

        cursor1.close();

        statistic[0] = countExecuteTask;
        statistic[1] = countOverdueTask;
        return statistic;
    }

    public static int[] queryBetweenDateStatistic(String date1, String date2, SQLiteDatabase taskDB) {

        Cursor cursor = taskDB.query(TABLE_TIMETABLESOLVE, null, COLUMN_TIMETABLESOLVE_DATE + " BETWEEN ? AND ?", new String[]{
                date1, date2}, null, null, null, null);

        Log.d(LOG_TAG, "Date1 = " + date1);
        Log.d(LOG_TAG, "Date2 = " + date2);

        int statistic[] = new int[2];
        int countExecuteTask = 0;
        int countOverdueTask = 0;
        Date dateFromDB, secondDate, firstDate;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    for (String columnNames : cursor.getColumnNames()) {
                        if (columnNames.matches("idTimetableSolve")) {
                            continue;
                        } else if(columnNames.matches("date")){
                            int dateColIndex = cursor.getColumnIndex(columnNames);
                            String date = cursor.getString(dateColIndex);
                            try {
                                dateFromDB = format.parse(date);
                                firstDate = format.parse(date1);
                                secondDate = format.parse(date2);
                                if (dateFromDB.before(firstDate) || dateFromDB.after(secondDate)) {
                                    break;
                                }
                            }catch (Exception e){
                                Log.d(LOG_TAG, "Exception parse date");
                            }
                        } else {
                            int isSolvedColIndex = cursor.getColumnIndex(columnNames);
                            boolean isSolved = (cursor.getInt(isSolvedColIndex) != 0);
                            if (isSolved) {
                                countExecuteTask++;
                            } else {
                                countOverdueTask++;
                            }
                        }
                    }
                } while (cursor.moveToNext());
            }
        }

        cursor.close();

        Cursor cursor1 = taskDB.query(TABLE_TIMETABLE, null, COLUMN_TIMETABLESOLVE_DATE + " BETWEEN ? AND ?", new String[]{
                date1, date2}, null, null, null, null);

        if (cursor1 != null) {
            if (cursor1.moveToFirst()) {
                do {
                    for (String columnNames : cursor1.getColumnNames()) {
                        if (columnNames.matches("idTimetable")) {
                            continue;
                        }else if(columnNames.matches("date")){
                            int dateColIndex = cursor1.getColumnIndex(columnNames);
                            String date = cursor1.getString(dateColIndex);
                            try {
                                dateFromDB = format.parse(date);
                                firstDate = format.parse(date1);
                                secondDate = format.parse(date2);
                                if (dateFromDB.before(firstDate) || dateFromDB.after(secondDate)) {
                                    break;
                                }
                            }catch (Exception e){
                                Log.d(LOG_TAG, "Exception parse date");
                            }
                        } else {
                            int taskId = Integer.parseInt(cursor1.getString(cursor1.getColumnIndex(columnNames)));
                            if (taskId == 1) {
                                countOverdueTask--;
                            }
                        }
                    }
                } while (cursor1.moveToNext());
            }
        }

        cursor1.close();

        statistic[0] = countExecuteTask;
        statistic[1] = countOverdueTask;
        return statistic;
    }
}
