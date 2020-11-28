package `in`.kashewdevelopers.todo.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GroupDbHelper(private val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "groups"
        const val DB_VERSION = 1

        const val COLUMN_ID = "_id"
        const val COLUMN_TITLE = "groupName"
        const val COLUMN_TASK_CREATING_TIME = "taskCreatedAt"
        const val COLUMN_COUNT = "taskCount"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = "CREATE TABLE $DB_NAME(" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT UNIQUE NOT NULL, " +
                "$COLUMN_TASK_CREATING_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "$COLUMN_COUNT INTEGER DEFAULT 0);"

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}


    // tasks
    fun insertGroup(db: SQLiteDatabase, title: String): Boolean {
        val contentValue = ContentValues()
        contentValue.put(COLUMN_TITLE, title)

        return db.insert(DB_NAME, null, contentValue) != -1L
    }

    fun queryGroup(db: SQLiteDatabase): Cursor = db.query(DB_NAME, null, null,
            null, null, null, null)


    fun deleteGroup(db: SQLiteDatabase, groupName: String) {
        val condition = "$COLUMN_TITLE = ?"
        db.delete(DB_NAME, condition, arrayOf(groupName))

        deleteTasks(groupName)
    }

    fun updateGroup(db: SQLiteDatabase, oldGroupName: String, newGroupName: String) {
        val data = ContentValues()
        data.put(COLUMN_TITLE, newGroupName)

        val condition = "$COLUMN_TITLE = ?"
        db.update(DB_NAME, data, condition, arrayOf(oldGroupName))

        updateTasks(oldGroupName, newGroupName)
    }


    private fun deleteTasks(groupName: String) {
        val taskDbHelper = TaskDbHelper(context)
        val taskDb = taskDbHelper.writableDatabase

        val condition = "${TaskDbHelper.COLUMN_GROUP} = ?"
        taskDb.delete(TaskDbHelper.DB_NAME, condition, arrayOf(groupName))
    }

    private fun updateTasks(oldGroupName: String, newGroupName: String) {
        val taskDbHelper = TaskDbHelper(context)
        val taskDb = taskDbHelper.writableDatabase

        val data = ContentValues()
        data.put(TaskDbHelper.COLUMN_GROUP, newGroupName)

        val condition = "${TaskDbHelper.COLUMN_GROUP} = ?"
        taskDb.update(TaskDbHelper.DB_NAME, data, condition, arrayOf(oldGroupName))
    }

}
