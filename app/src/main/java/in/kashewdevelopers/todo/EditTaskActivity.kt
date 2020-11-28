package `in`.kashewdevelopers.todo

import `in`.kashewdevelopers.todo.databinding.ActivityAddTaskBinding
import `in`.kashewdevelopers.todo.db.TaskDbHelper
import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    private var db: SQLiteDatabase? = null
    private lateinit var dbHelper: TaskDbHelper

    private var id: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialize()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    // initialization
    private fun initialize() {
        dbHelper = TaskDbHelper(this)
        db = dbHelper.writableDatabase

        binding.addTask.setText(R.string.update)

        setBackButton()
        setData()
    }

    private fun setBackButton() {
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setData() {
        val intent = intent

        id = intent.getIntExtra(TaskDbHelper.COLUMN_ID, -1)

        binding.taskTitle.setText(intent.getStringExtra(TaskDbHelper.COLUMN_TITLE))
        binding.taskDetails.setText(intent.getStringExtra(TaskDbHelper.COLUMN_DETAIL))
        binding.dailyTask.isChecked = intent.getBooleanExtra(TaskDbHelper.COLUMN_DAILY_TASK, false)
        binding.showCount.isChecked = intent.getBooleanExtra(TaskDbHelper.COLUMN_SHOW_COUNT, false)

        onDailyTaskClicked(binding.dailyTask)
    }


    // handle widget clicks
    fun onAddClicked(view: View) {
        if (!verifyTitle()) {
            return
        }

        val tempDb = db
        tempDb ?: return

        dbHelper.updateTask(tempDb, id, binding.taskTitle.text.toString(),
                binding.taskDetails.text.toString(),
                binding.dailyTask.isChecked, binding.showCount.isChecked)
        dbHelper.close()

        val taskUpdated = Toast.makeText(this, R.string.task_updated, Toast.LENGTH_LONG)
        taskUpdated.setGravity(Gravity.CENTER, 0, 0)
        taskUpdated.show()

        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun onDailyTaskClicked(view: View) {
        if (binding.dailyTask.isChecked) {
            binding.showCount.visibility = View.VISIBLE
        } else {
            binding.showCount.isChecked = false
            binding.showCount.visibility = View.GONE
        }
    }


    // functionality
    private fun verifyTitle(): Boolean {
        if (binding.taskTitle.length() < 1) {
            binding.taskTitle.error = getText(R.string.cannot_be_empty)
            binding.taskTitle.requestFocus()
            return false
        }

        return true
    }

}