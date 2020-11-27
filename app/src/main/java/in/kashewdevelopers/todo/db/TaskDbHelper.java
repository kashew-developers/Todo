package in.kashewdevelopers.todo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.kashewdevelopers.todo.provider_classes.Constants;
import in.kashewdevelopers.todo.provider_classes.PrefManager;

public class TaskDbHelper extends SQLiteOpenHelper {

    private Context context;

    public static String DB_NAME = "tasks";
    public static int DB_VERSION = 1;

    public static String COLUMN_ID = "_id";
    public static String COLUMN_TITLE = "title";
    public static String COLUMN_DETAIL = "detail";
    public static String COLUMN_TASK_CREATING_TIME = "taskCreatedAt";
    public static String COLUMN_COUNT = "count";
    public static String COLUMN_SHOW_COUNT = "shouldShowCount";
    public static String COLUMN_DAILY_TASK = "dailyTask";
    public static String COLUMN_TOTAL = "total";
    public static String COLUMN_GROUP = "groupName";
    public static String COLUMN_LAST_COMPLETED_TIME = "lastCompletedAt";

    public TaskDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + DB_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_DETAIL + " TEXT, " +
                COLUMN_GROUP + " TEXT, " +
                COLUMN_TASK_CREATING_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_LAST_COMPLETED_TIME + " TIMESTAMP DEFAULT NULL, " +
                COLUMN_COUNT + " INTEGET DEFAULT 0, " +
                COLUMN_SHOW_COUNT + " INTEGET DEFAULT 0, " +
                COLUMN_DAILY_TASK + " INTEGET DEFAULT 0, " +
                COLUMN_TOTAL + " INTEGET DEFAULT 0);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public void insertTask(SQLiteDatabase db, String title, String detail, String group,
                           boolean dailyTask, boolean showCount) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(COLUMN_TITLE, title);
        contentValue.put(COLUMN_DETAIL, detail);
        contentValue.put(COLUMN_GROUP, group);
        contentValue.put(COLUMN_DAILY_TASK, dailyTask ? 1 : 0);
        contentValue.put(COLUMN_SHOW_COUNT, showCount ? 1 : 0);

        db.insert(DB_NAME, null, contentValue);

        incrementGroupTaskCount(group);
    }

    public Cursor queryTask(SQLiteDatabase db, String groupName) {
        String sortOrder = getSortOrder();
        String condition = COLUMN_GROUP + " = ?";
        String[] selectionArgs = new String[]{groupName};

        return db.query(DB_NAME, null, condition, selectionArgs, null, null, sortOrder);
    }

    public void updateTaskCompletionState(SQLiteDatabase db, int id, boolean completed) {
        String completionStateValue = completed ? "CURRENT_TIMESTAMP" : "null";
        String countChange = completed ? " + 1" : " - 1";

        db.execSQL("UPDATE " + DB_NAME +
                " SET " + COLUMN_LAST_COMPLETED_TIME + " = " + completionStateValue + ", " +
                COLUMN_COUNT + " = " + COLUMN_COUNT + countChange +
                " WHERE " + COLUMN_ID + " = " + id + "");
    }

    public void deleteTask(SQLiteDatabase db, int id, String group) {
        String condition = COLUMN_ID + " = ?";
        db.delete(DB_NAME, condition, new String[]{String.valueOf(id)});

        decrementGroupTaskCount(group);
    }

    public void updateTask(SQLiteDatabase db, int id, String title, String details,
                           boolean dailyTask, boolean showCount) {
        ContentValues data = new ContentValues();
        data.put(COLUMN_TITLE, title);
        data.put(COLUMN_DETAIL, details);
        data.put(COLUMN_DAILY_TASK, dailyTask ? 1 : 0);
        data.put(COLUMN_SHOW_COUNT, showCount ? 1 : 0);


        String condition = COLUMN_ID + " = ?";

        db.update(DB_NAME, data, condition, new String[]{String.valueOf(id)});
    }

    public void clearAllTasks(SQLiteDatabase db, String groupName) {
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_COMPLETED, 0);
//
//        String condition = COLUMN_GROUP + " = ?";
//        String[] conditionArgs = new String[]{groupName};
//
//        db.update(DB_NAME, values, condition, conditionArgs);
    }


    public String getSortOrder() {
        int sortCode = PrefManager.getSortType(context);
        String sortOrder = COLUMN_TITLE;

        switch (sortCode) {
            case Constants.SORT_A_Z:
                sortOrder = COLUMN_TITLE;
                break;
            case Constants.SORT_Z_A:
                sortOrder = COLUMN_TITLE + " DESC";
                break;

            case Constants.SORT_PENDING_COMPLETE:
                sortOrder = COLUMN_LAST_COMPLETED_TIME;
                break;
            case Constants.SORT_COMPLETE_PENDING:
                sortOrder = COLUMN_LAST_COMPLETED_TIME + " DESC";
                break;

            case Constants.SORT_OLD_EARLY:
                sortOrder = COLUMN_TASK_CREATING_TIME;
                break;
            case Constants.SORT_EARLY_OLD:
                sortOrder = COLUMN_TASK_CREATING_TIME + " DESC";
                break;
        }

        return sortOrder;
    }


    private void incrementGroupTaskCount(String group) {
        GroupDbHelper groupDbHelper = new GroupDbHelper(context);
        SQLiteDatabase groupDb = groupDbHelper.getWritableDatabase();

        groupDb.execSQL("UPDATE " + GroupDbHelper.DB_NAME +
                " SET " + GroupDbHelper.COLUMN_COUNT + " = " + GroupDbHelper.COLUMN_COUNT + " + 1" +
                " where " + GroupDbHelper.COLUMN_TITLE + " = '" + group + "'");
    }

    private void decrementGroupTaskCount(String group) {
        GroupDbHelper groupDbHelper = new GroupDbHelper(context);
        SQLiteDatabase groupDb = groupDbHelper.getWritableDatabase();

        groupDb.execSQL("UPDATE " + GroupDbHelper.DB_NAME +
                " SET " + GroupDbHelper.COLUMN_COUNT + " = " + GroupDbHelper.COLUMN_COUNT + " - 1" +
                " where " + GroupDbHelper.COLUMN_TITLE + " = '" + group + "'");
    }

}
