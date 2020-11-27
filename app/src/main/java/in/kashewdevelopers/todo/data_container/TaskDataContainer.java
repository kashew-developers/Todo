package in.kashewdevelopers.todo.data_container;

import android.database.Cursor;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import in.kashewdevelopers.todo.db.TaskDbHelper;

public class TaskDataContainer {

    public String title, details;
    public int count, total, id;
    public boolean isCompleted;
    public boolean dailyTask;
    public boolean showCount;
    public long taskCreationTime;

    public TaskDataContainer(Cursor cursor) {
        title = cursor.getString(cursor.getColumnIndex(TaskDbHelper.COLUMN_TITLE));
        details = cursor.getString(cursor.getColumnIndex(TaskDbHelper.COLUMN_DETAIL));

        id = cursor.getInt(cursor.getColumnIndex(TaskDbHelper.COLUMN_ID));

        count = cursor.getInt(cursor.getColumnIndex(TaskDbHelper.COLUMN_COUNT));
        total = cursor.getInt(cursor.getColumnIndex(TaskDbHelper.COLUMN_TOTAL));

        dailyTask = cursor.getInt(cursor.getColumnIndex(TaskDbHelper.COLUMN_DAILY_TASK)) == 1;
        showCount = cursor.getInt(cursor.getColumnIndex(TaskDbHelper.COLUMN_SHOW_COUNT)) == 1;

        if (dailyTask) {
            String timestamp = cursor.getString(cursor.getColumnIndex(TaskDbHelper.COLUMN_LAST_COMPLETED_TIME));
            if (timestamp == null) {
                isCompleted = false;
            } else {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                int todayDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Timestamp.valueOf(timestamp).getTime());
                int taskDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

                isCompleted = todayDayOfYear == taskDayOfYear;
            }
        } else {
            isCompleted = (cursor.getString(cursor.getColumnIndex(TaskDbHelper.COLUMN_LAST_COMPLETED_TIME)) != null);
        }

        taskCreationTime = Timestamp.valueOf(cursor.getString(
                cursor.getColumnIndex(TaskDbHelper.COLUMN_TASK_CREATING_TIME))).getTime();
    }

}
