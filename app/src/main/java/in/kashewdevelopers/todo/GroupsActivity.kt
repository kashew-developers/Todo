package `in`.kashewdevelopers.todo

import `in`.kashewdevelopers.todo.adapters.GroupRecycleListAdapter
import `in`.kashewdevelopers.todo.adapters.GroupViewHolder
import `in`.kashewdevelopers.todo.databinding.ActivityGroupsBinding
import `in`.kashewdevelopers.todo.databinding.AddGroupDialogBinding
import `in`.kashewdevelopers.todo.databinding.EditGroupDialogBinding
import `in`.kashewdevelopers.todo.db.GroupDbHelper
import `in`.kashewdevelopers.todo.provider_classes.Constants
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

class GroupsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupsBinding

    private var cursor: Cursor? = null
    private var db: SQLiteDatabase? = null
    private lateinit var dbHelper: GroupDbHelper
    private lateinit var adapter: GroupRecycleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialize()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.group_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addOption) {
            onAddGroupClicked()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.ACTIVITY_TASK_LIST) {
            initializeCursor()
            initializeAdapter()
            initializeList()
        }
    }


    // initialization
    private fun initialize() {
        dbHelper = GroupDbHelper(this)
        db = dbHelper.readableDatabase

        initializeCursor()
        initializeAdapter()
        initializeList()
    }

    private fun initializeCursor() {
        db?.let {
            cursor = dbHelper.queryGroup(it)
            cursor?.let { c -> binding.noGroups.visibility = if (c.count < 1) View.VISIBLE else View.GONE }
                    ?: run { binding.noGroups.visibility = View.GONE }
        }
    }

    private fun initializeAdapter() {
        val tempCursor = cursor
        tempCursor ?: return

        if (!this::adapter.isInitialized) {
            adapter = GroupRecycleListAdapter(tempCursor, this)
                    .setOnItemClickListener(object : GroupRecycleListAdapter.OnItemClickListener {
                        override fun onItemClickListener(holder: GroupViewHolder, showDetail: Boolean) {
                            onItemClicked(holder, showDetail)
                        }
                    })
        } else {
            adapter.updateCursor(tempCursor)
        }
    }

    private fun initializeList() {
        if (binding.groupList.adapter == null) {
            binding.groupList.layoutManager = LinearLayoutManager(this)
            binding.groupList.adapter = adapter
        }
    }


    // functionality
    private fun addGroup(groupName: String) {
        val tempDb = db
        tempDb ?: return
        if (dbHelper.insertGroup(tempDb, groupName)) {
            initializeCursor()
            initializeAdapter()
            initializeList()

            val groupCreated = Toast.makeText(this, R.string.group_added, Toast.LENGTH_LONG)
            groupCreated.setGravity(Gravity.CENTER, 0, 0)
            groupCreated.show()
        } else {
            val error = Toast.makeText(this, R.string.error_creating_group, Toast.LENGTH_LONG)
            error.setGravity(Gravity.CENTER, 0, 0)
            error.show()
        }
    }

    private fun deleteGroup(groupName: String) {
        val tempDb = db
        tempDb ?: return

        dbHelper.deleteGroup(tempDb, groupName)

        initializeCursor()
        initializeAdapter()
        initializeList()
    }

    private fun updateGroup(oldGroupName: String, newGroupName: String) {
        val tempDb = db
        tempDb ?: return

        dbHelper.updateGroup(tempDb, oldGroupName, newGroupName)

        initializeCursor()
        initializeAdapter()
        initializeList()
    }


    // handle widget clicks
    private fun onItemClicked(holder: GroupViewHolder, showDetail: Boolean) {
        if (showDetail) {
            with(AlertDialog.Builder(this)) {
                setTitle(holder.groupName)
                setPositiveButton(R.string.delete_task) { _, _ ->
                    onDeleteGroupClicked(holder.groupName ?: "")
                }
                setNeutralButton(R.string.close, null)
                setNegativeButton(R.string.edit) { _, _ ->
                    onEditGroupClicked(holder.groupName ?: "")
                }
                setCancelable(true)
                show()
            }
        } else {
            val intent = Intent(this, TasksActivity::class.java)
            intent.putExtra(Constants.GROUP, holder.groupName)
            startActivityForResult(intent, Constants.ACTIVITY_TASK_LIST)
        }
    }

    private fun onDeleteGroupClicked(groupName: String) {
        with(AlertDialog.Builder(this)) {
            setTitle(groupName)
            setMessage(R.string.delete_all_tasks_in_groups)
            setNegativeButton(R.string.cancel, null)
            setPositiveButton(R.string.delete_task) { _, _ -> deleteGroup(groupName) }
            show()
        }
    }

    private fun onEditGroupClicked(groupName: String) {
        val dialog = AlertDialog.Builder(this).create()

        val dialogBinding = EditGroupDialogBinding.inflate(layoutInflater)
        dialogBinding.groupName.setText(groupName)
        dialog.setView(dialogBinding.root)

        dialog.setTitle(R.string.change_group_name)
        dialog.setCancelable(true)

        dialogBinding.cancelButton.setOnClickListener { dialog.dismiss() }

        dialogBinding.updateButton.setOnClickListener {
            when {
                dialogBinding.groupName.length() < 1 -> {
                    dialogBinding.groupName.error = getText(R.string.cannot_be_empty)
                    dialogBinding.groupName.requestFocus()
                }
                groupName == dialogBinding.groupName.text.toString() -> {
                    dialog.dismiss()
                }
                else -> {
                    updateGroup(groupName, dialogBinding.groupName.text.toString())
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun onAddGroupClicked() {
        val dialog = AlertDialog.Builder(this).create()

        val dialogBinding = AddGroupDialogBinding.inflate(layoutInflater)
        dialog.setView(dialogBinding.root)

        dialog.setTitle(R.string.add_group)
        dialog.setCancelable(true)

        dialogBinding.cancelButton.setOnClickListener { dialog.dismiss() }

        dialogBinding.addGroupButton.setOnClickListener {
            if (dialogBinding.groupName.length() < 1) {
                dialogBinding.groupName.error = getText(R.string.cannot_be_empty)
                dialogBinding.groupName.requestFocus()
            } else {
                addGroup(dialogBinding.groupName.text.toString())
                dialog.dismiss()
            }
        }

        dialog.show()
    }

}