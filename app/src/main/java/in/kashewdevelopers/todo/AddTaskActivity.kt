package `in`.kashewdevelopers.todo

import `in`.kashewdevelopers.todo.databinding.ActivityAddTaskBinding
import `in`.kashewdevelopers.todo.db.TaskDbHelper
import `in`.kashewdevelopers.todo.provider_classes.Constants
import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    private var db: SQLiteDatabase? = null
    private lateinit var dbHelper: TaskDbHelper

    private lateinit var groupName: String

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
        val intent = intent
        groupName = intent.getStringExtra(Constants.GROUP) ?: run {
            finish()
            return
        }

        dbHelper = TaskDbHelper(this)
        db = dbHelper.writableDatabase

        setBackButton()
    }

    private fun setBackButton() {
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }


    // handle widget clicks
    fun onAddClicked(view: View) {
        if (!verifyTitle()) {
            return
        }

        dbHelper.insertTask(db, binding.taskTitle.text.toString(),
                binding.taskDetails.text.toString(), groupName,
                binding.dailyTask.isChecked, binding.showCount.isChecked)
        dbHelper.close()

        val taskAdded = Toast.makeText(this, R.string.task_added, Toast.LENGTH_LONG)
        taskAdded.setGravity(Gravity.CENTER, 0, 0)
        taskAdded.show()

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