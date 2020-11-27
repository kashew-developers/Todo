package in.kashewdevelopers.todo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GroupDbHelper extends SQLiteOpenHelper {

    private Context context;

    public static String DB_NAME = "groups";
    public static int DB_VERSION = 1;

    public static String COLUMN_ID = "_id";
    public static String COLUMN_TITLE = "groupName";
    public static String COLUMN_TASK_CREATING_TIME = "taskCreatedAt";
    public static String COLUMN_COUNT = "taskCount";

    public GroupDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + DB_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT UNIQUE NOT NULL, " +
                COLUMN_TASK_CREATING_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_COUNT + " INTEGET DEFAULT 0);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    // tasks
    public boolean insertGroup(SQLiteDatabase db, String title) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(COLUMN_TITLE, title);

        return db.insert(DB_NAME, null, contentValue) != -1;
    }

    public Cursor queryGroup(SQLiteDatabase db) {
        return db.query(DB_NAME, null, null, null, null, null, null);
    }

    public void deleteGroup(SQLiteDatabase db, String groupName) {
        String condition = COLUMN_TITLE + " = ?";
        db.delete(DB_NAME, condition, new String[]{groupName});

        deleteTasks(groupName);
    }

    public void updateGroup(SQLiteDatabase db, String oldGroupName, String newGroupName) {
        ContentValues data = new ContentValues();
        data.put(COLUMN_TITLE, newGroupName);

        String condition = COLUMN_TITLE + " = ?";
        db.update(DB_NAME, data, condition, new String[]{oldGroupName});

        updateTasks(oldGroupName, newGroupName);
    }


    private void deleteTasks(String groupName) {
        TaskDbHelper taskDbHelper = new TaskDbHelper(context);
        SQLiteDatabase taskDb = taskDbHelper.getWritableDatabase();

        String condition = TaskDbHelper.COLUMN_GROUP + " = ?";
        taskDb.delete(TaskDbHelper.DB_NAME, condition, new String[]{groupName});
    }

    private void updateTasks(String oldGroupName, String newGroupName) {
        TaskDbHelper taskDbHelper = new TaskDbHelper(context);
        SQLiteDatabase taskDb = taskDbHelper.getWritableDatabase();

        ContentValues data = new ContentValues();
        data.put(TaskDbHelper.COLUMN_GROUP, newGroupName);

        String condition = TaskDbHelper.COLUMN_GROUP + " = ?";
        taskDb.update(TaskDbHelper.DB_NAME, data, condition, new String[]{oldGroupName});
    }

}
