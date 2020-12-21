package `in`.kashewdevelopers.todo.data_container

import `in`.kashewdevelopers.todo.db.TaskDbHelper
import android.database.Cursor
import java.sql.Timestamp
import java.util.*
import kotlin.math.ceil

class TaskDataContainer(cursor: Cursor) {

    val title: String = cursor.getString(cursor.getColumnIndex(TaskDbHelper.COLUMN_TITLE))
    val details: String = cursor.getString(cursor.getColumnIndex(TaskDbHelper.COLUMN_DETAIL))

    val id = cursor.getInt(cursor.getColumnIndex(TaskDbHelper.COLUMN_ID))
    val count = cursor.getInt(cursor.getColumnIndex(TaskDbHelper.COLUMN_COUNT))
    val total: Int

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
                val todayCal = Calendar.getInstance()
                val todayDate = "${todayCal.get(Calendar.YEAR)}.${todayCal.get(Calendar.MONTH)}.${todayCal.get(Calendar.DAY_OF_MONTH)}"

                todayCal.timeInMillis = Timestamp.valueOf(timestamp).time

                val taskCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
                taskCal.set(Calendar.YEAR, todayCal.get(Calendar.YEAR))
                taskCal.set(Calendar.MONTH, todayCal.get(Calendar.MONTH))
                taskCal.set(Calendar.DAY_OF_YEAR, todayCal.get(Calendar.DAY_OF_YEAR))
                taskCal.set(Calendar.HOUR_OF_DAY, todayCal.get(Calendar.HOUR_OF_DAY))
                taskCal.set(Calendar.MINUTE, todayCal.get(Calendar.MINUTE))
                taskCal.set(Calendar.SECOND, todayCal.get(Calendar.SECOND))

                taskCal.time
                taskCal.timeZone = TimeZone.getDefault()
                val taskDate = "${taskCal.get(Calendar.YEAR)}.${taskCal.get(Calendar.MONTH)}.${taskCal.get(Calendar.DAY_OF_MONTH)}"

                isCompleted = (todayDate == taskDate)
            }
        } else {
            isCompleted = (cursor.getString(cursor.getColumnIndex(TaskDbHelper.COLUMN_LAST_COMPLETED_TIME)) != null)
        }

        taskCreationTime = Timestamp.valueOf(cursor.getString(
                cursor.getColumnIndex(TaskDbHelper.COLUMN_TASK_CREATING_TIME))).time

        val todayCal = Calendar.getInstance()
        todayCal.timeInMillis = taskCreationTime

        val taskCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        taskCal.set(Calendar.YEAR, todayCal.get(Calendar.YEAR))
        taskCal.set(Calendar.MONTH, todayCal.get(Calendar.MONTH))
        taskCal.set(Calendar.DAY_OF_YEAR, todayCal.get(Calendar.DAY_OF_YEAR))

        taskCal.time
        taskCal.timeZone = TimeZone.getDefault()

        taskCal.set(Calendar.HOUR_OF_DAY, 0)
        taskCal.set(Calendar.MINUTE, 0)
        taskCal.set(Calendar.SECOND, 0)

        total = ceil((System.currentTimeMillis() - taskCal.time.time) / 1000.0 / 60 / 60 / 24).toInt()
    }

}
