package `in`.kashewdevelopers.todo.data_container

import `in`.kashewdevelopers.todo.db.TaskDbHelper
import android.database.Cursor
import java.sql.Timestamp
import java.util.*

class TaskDataContainer(cursor: Cursor) {


    val title: String = cursor.getString(cursor.getColumnIndex(TaskDbHelper.COLUMN_TITLE))
    val details: String = cursor.getString(cursor.getColumnIndex(TaskDbHelper.COLUMN_DETAIL))

    val id = cursor.getInt(cursor.getColumnIndex(TaskDbHelper.COLUMN_ID))
    val count = cursor.getInt(cursor.getColumnIndex(TaskDbHelper.COLUMN_COUNT))
    val total = cursor.getInt(cursor.getColumnIndex(TaskDbHelper.COLUMN_TOTAL))

    val dailyTask = cursor.getInt(cursor.getColumnIndex(TaskDbHelper.COLUMN_DAILY_TASK)) == 1
    val showCount = cursor.getInt(cursor.getColumnIndex(TaskDbHelper.COLUMN_SHOW_COUNT)) == 1

    val isCompleted: Boolean
    val taskCreationTime: Long

    init {

        if (dailyTask) {
            val timestamp = cursor.getString(cursor.getColumnIndex(TaskDbHelper.COLUMN_LAST_COMPLETED_TIME))
            if (timestamp == null) {
                isCompleted = false
            } else {
                var calendar = Calendar.getInstance(TimeZone.getDefault())
                val todayDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

                calendar = Calendar.getInstance(TimeZone.getDefault())
                calendar.timeInMillis = Timestamp.valueOf(timestamp).time
                val taskDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

                isCompleted = todayDayOfYear == taskDayOfYear
            }
        } else {
            isCompleted = (cursor.getString(cursor.getColumnIndex(TaskDbHelper.COLUMN_LAST_COMPLETED_TIME)) != null)
        }

        taskCreationTime = Timestamp.valueOf(cursor.getString(
                cursor.getColumnIndex(TaskDbHelper.COLUMN_TASK_CREATING_TIME))).time
    }

}
