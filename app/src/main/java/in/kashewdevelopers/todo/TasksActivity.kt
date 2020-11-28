package `in`.kashewdevelopers.todo

import `in`.kashewdevelopers.todo.adapters.TaskRecycleListAdapter
import `in`.kashewdevelopers.todo.adapters.TaskViewHolder
import `in`.kashewdevelopers.todo.databinding.ActivityTasksBinding
import `in`.kashewdevelopers.todo.db.TaskDbHelper
import `in`.kashewdevelopers.todo.provider_classes.Constants
import `in`.kashewdevelopers.todo.provider_classes.PrefManager
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

class TasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTasksBinding

    private var db: SQLiteDatabase? = null
    private lateinit var dbHelper: TaskDbHelper
    private var cursor: Cursor? = null
    private lateinit var adapter: TaskRecycleListAdapter

    private var groupName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialize()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.task_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item.itemId == R.id.addOption -> {
                val intent = Intent(this, AddTaskActivity::class.java)
                intent.putExtra(Constants.GROUP, groupName)
                startActivityForResult(intent, Constants.ACTIVITY_ADD_TASK)
                return true
            }
            item.itemId == android.R.id.home -> {
                onBackPressed()
                return true
            }
            sortOptionSelected(item.itemId) -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == Constants.ACTIVITY_ADD_TASK ||
                        requestCode == Constants.ACTIVITY_EDIT_TASK) &&
                resultCode == Activity.RESULT_OK) {
            initializeCursor()
            initializeAdapter()
            initializeTaskList()
        }
    }


    // initialization
    private fun initialize() {
        val intent = intent

        groupName = intent.getStringExtra(Constants.GROUP) ?: ""

        dbHelper = TaskDbHelper(this)
        db = dbHelper.readableDatabase

        configureActionBar()

        initializeCursor()
        initializeAdapter()
        initializeTaskList()
    }

    private fun initializeCursor() {
        db?.let {
            cursor = dbHelper.queryTask(it, groupName)
            binding.noTasks.visibility =
                    cursor?.let { c -> if (c.count < 1) View.VISIBLE else View.GONE }
                            ?: View.GONE
        }
    }

    private fun initializeAdapter() {
        val tempCursor = cursor
        tempCursor ?: return

        if (!::adapter.isInitialized) {
            adapter = TaskRecycleListAdapter(tempCursor, this)
                    .setOnItemClickListener(object : TaskRecycleListAdapter.OnItemClickListener {
                        override fun onItemClickListener(holder: TaskViewHolder, showDetail: Boolean) {
                            onItemClicked(holder, showDetail)
                        }
                    })
        } else {
            adapter.updateCursor(tempCursor)
        }
    }

    private fun initializeTaskList() {
        if (binding.taskList.adapter == null) {
            binding.taskList.layoutManager = LinearLayoutManager(this)
            binding.taskList.adapter = adapter
        }
    }

    private fun configureActionBar() {
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.title = groupName
        }
    }


    // handle widget clicks
    private fun onItemClicked(holder: TaskViewHolder, showDetail: Boolean) {
        if (showDetail) {
            with(AlertDialog.Builder(this)) {
                setTitle(holder.data.title)
                setMessage(holder.data.details)
                setPositiveButton(R.string.delete_task) { _, _ -> onDeleteTaskClicked(holder.data.id) }
                setNeutralButton(R.string.close, null)
                setNegativeButton(R.string.edit) { _, _ -> onEditTaskClicked(holder) }
                setCancelable(true)
                show()
            }
        } else {
            db?.let {
                dbHelper.updateTaskCompletionState(it, holder.data.id, !holder.data.isCompleted)

                initializeCursor()
                initializeAdapter()
            }
        }
    }

    private fun onDeleteTaskClicked(taskId: Int) {
        db?.let {
            dbHelper.deleteTask(it, taskId, groupName)

            initializeCursor()
            initializeAdapter()
        }
    }

    private fun onEditTaskClicked(holder: TaskViewHolder) {
        val intent = Intent(this, EditTaskActivity::class.java)
        with(intent) {
            putExtra(TaskDbHelper.COLUMN_ID, holder.data.id)
            putExtra(TaskDbHelper.COLUMN_TITLE, holder.data.title)
            putExtra(TaskDbHelper.COLUMN_DETAIL, holder.data.details)
            putExtra(TaskDbHelper.COLUMN_SHOW_COUNT, holder.data.showCount)
            putExtra(TaskDbHelper.COLUMN_DAILY_TASK, holder.data.dailyTask)
        }
        startActivityForResult(intent, Constants.ACTIVITY_EDIT_TASK)
    }


    // functionality
    private fun sortOptionSelected(itemId: Int): Boolean {
        when (itemId) {
            R.id.atozOption -> PrefManager.setSortType(this, Constants.SORT_A_Z)
            R.id.ztoaOption -> PrefManager.setSortType(this, Constants.SORT_Z_A)
            R.id.earlytooldOption -> PrefManager.setSortType(this, Constants.SORT_EARLY_OLD)
            R.id.oldtoearlyOption -> PrefManager.setSortType(this, Constants.SORT_OLD_EARLY)
            R.id.completedtopendingOption -> PrefManager.setSortType(this, Constants.SORT_COMPLETE_PENDING)
            R.id.pendingtocompletedOption -> PrefManager.setSortType(this, Constants.SORT_PENDING_COMPLETE)
            else -> return false
        }

        initializeCursor()
        initializeAdapter()
        initializeTaskList()
        return true
    }

}