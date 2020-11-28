package `in`.kashewdevelopers.todo.db

import `in`.kashewdevelopers.todo.provider_classes.Constants
import `in`.kashewdevelopers.todo.provider_classes.PrefManager
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDbHelper(private val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "tasks"
        const val DB_VERSION = 1

        const val COLUMN_ID = "_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DETAIL = "detail"
        const val COLUMN_TASK_CREATING_TIME = "taskCreatedAt"
        const val COLUMN_COUNT = "count"
        const val COLUMN_SHOW_COUNT = "shouldShowCount"
        const val COLUMN_DAILY_TASK = "dailyTask"
        const val COLUMN_TOTAL = "total"
        const val COLUMN_GROUP = "groupName"
        const val COLUMN_LAST_COMPLETED_TIME = "lastCompletedAt"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = "CREATE TABLE $DB_NAME(" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT NOT NULL, " +
                "$COLUMN_DETAIL TEXT, " +
                "$COLUMN_GROUP TEXT, " +
                "$COLUMN_TASK_CREATING_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "$COLUMN_LAST_COMPLETED_TIME TIMESTAMP DEFAULT NULL, " +
                "$COLUMN_COUNT INTEGER DEFAULT 0, " +
                "$COLUMN_SHOW_COUNT INTEGER DEFAULT 0, " +
                "$COLUMN_DAILY_TASK INTEGER DEFAULT 0, " +
                "$COLUMN_TOTAL INTEGER DEFAULT 0);"

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}


    fun insertTask(db: SQLiteDatabase, title: String, detail: String, group: String,
                   dailyTask: Boolean, showCount: Boolean) {
        val contentValue = ContentValues()
        with(contentValue) {
            put(COLUMN_TITLE, title)
            put(COLUMN_DETAIL, detail)
            put(COLUMN_GROUP, group)
            put(COLUMN_DAILY_TASK, if (dailyTask) 1 else 0)
            put(COLUMN_SHOW_COUNT, if (showCount) 1 else 0)
        }
        db.insert(DB_NAME, null, contentValue)

        incrementGroupTaskCount(group)
    }

    fun queryTask(db: SQLiteDatabase, groupName: String): Cursor {
        val sortOrder = getSortOrder()
        val condition = "$COLUMN_GROUP = ?"
        val selectionArgs = arrayOf(groupName)

        return db.query(DB_NAME, null, condition, selectionArgs, null, null, sortOrder)
    }

    fun updateTaskCompletionState(db: SQLiteDatabase, id: Int, completed: Boolean) {
        val completionStateValue = if (completed) "CURRENT_TIMESTAMP" else "null"
        val countChange = if (completed) " + 1" else " - 1"

        db.execSQL("UPDATE $DB_NAME" +
                " SET $COLUMN_LAST_COMPLETED_TIME = $completionStateValue, " +
                "$COLUMN_COUNT = $COLUMN_COUNT $countChange" +
                " WHERE $COLUMN_ID = $id")
    }

    fun deleteTask(db: SQLiteDatabase, id: Int, group: String) {
        val condition = "$COLUMN_ID = ?"
        db.delete(DB_NAME, condition, arrayOf("$id"))

        decrementGroupTaskCount(group)
    }

    fun updateTask(db: SQLiteDatabase, id: Int, title: String, details: String,
                   dailyTask: Boolean, showCount: Boolean) {
        val data = ContentValues()
        with(data) {
            put(COLUMN_TITLE, title)
            put(COLUMN_DETAIL, details)
            put(COLUMN_DAILY_TASK, if (dailyTask) 1 else 0)
            put(COLUMN_SHOW_COUNT, if (showCount) 1 else 0)
        }

        val condition = "$COLUMN_ID = ?"

        db.update(DB_NAME, data, condition, arrayOf("$id"))
    }

    private fun getSortOrder(): String {
        val sortCode = PrefManager.getSortType(context)
        var sortOrder = COLUMN_TITLE

        when (sortCode) {
            Constants.SORT_A_Z -> sortOrder = COLUMN_TITLE
            Constants.SORT_Z_A -> sortOrder = "$COLUMN_TITLE DESC"
            Constants.SORT_PENDING_COMPLETE -> sortOrder = COLUMN_LAST_COMPLETED_TIME
            Constants.SORT_COMPLETE_PENDING -> sortOrder = "$COLUMN_LAST_COMPLETED_TIME DESC"
            Constants.SORT_OLD_EARLY -> sortOrder = COLUMN_TASK_CREATING_TIME
            Constants.SORT_EARLY_OLD -> sortOrder = "$COLUMN_TASK_CREATING_TIME= DESC"
        }
        return sortOrder
    }


    private fun incrementGroupTaskCount(group: String) {
        val groupDbHelper = GroupDbHelper(context)
        val groupDb = groupDbHelper.writableDatabase

        groupDb.execSQL("UPDATE ${GroupDbHelper.DB_NAME}" +
                " SET ${GroupDbHelper.COLUMN_COUNT} = ${GroupDbHelper.COLUMN_COUNT} + 1" +
                " where ${GroupDbHelper.COLUMN_TITLE} = '${group}'")
    }

    private fun decrementGroupTaskCount(group: String) {
        val groupDbHelper = GroupDbHelper(context)
        val groupDb = groupDbHelper.writableDatabase

        groupDb.execSQL("UPDATE ${GroupDbHelper.DB_NAME}" +
                " SET ${GroupDbHelper.COLUMN_COUNT} = ${GroupDbHelper.COLUMN_COUNT} - 1" +
                " where ${GroupDbHelper.COLUMN_TITLE} = '${group}'")
    }

}
