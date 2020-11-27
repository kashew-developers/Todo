package in.kashewdevelopers.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import in.kashewdevelopers.todo.adapters.TaskRecycleListAdapter;
import in.kashewdevelopers.todo.adapters.TaskViewHolder;
import in.kashewdevelopers.todo.databinding.ActivityTasksBinding;
import in.kashewdevelopers.todo.db.TaskDbHelper;
import in.kashewdevelopers.todo.provider_classes.Constants;
import in.kashewdevelopers.todo.provider_classes.PrefManager;

public class TasksActivity extends AppCompatActivity {

    private ActivityTasksBinding binding;

    private SQLiteDatabase db;
    private TaskDbHelper dbHelper;
    private Cursor cursor;
    private TaskRecycleListAdapter adapter;

    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTasksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addOption) {
            Intent intent = new Intent(this, AddTaskActivity.class);
            intent.putExtra(Constants.GROUP, groupName);
            startActivityForResult(intent, Constants.ACTIVITY_ADD_TASK);
            return true;
        } else if (sortOptionSelected(item.getItemId())) {
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.clearTasksOption) {
            dbHelper.clearAllTasks(db, groupName);

            initializeCursor();
            initializeAdapter();
            initializeTaskList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == Constants.ACTIVITY_ADD_TASK ||
                requestCode == Constants.ACTIVITY_EDIT_TASK) &&
                resultCode == Activity.RESULT_OK) {
            initializeCursor();
            initializeAdapter();
            initializeTaskList();
        }
    }


    // initialization
    public void initialize() {
        Intent intent = getIntent();
        groupName = intent.getStringExtra(Constants.GROUP);

        dbHelper = new TaskDbHelper(this);
        db = dbHelper.getReadableDatabase();

        setActionBar();

        initializeCursor();
        initializeAdapter();
        initializeTaskList();
    }

    public void initializeCursor() {
        cursor = dbHelper.queryTask(db, groupName);
        binding.noTasks.setVisibility(cursor.getCount() < 1 ? View.VISIBLE : View.GONE);
    }

    public void initializeAdapter() {
        if (adapter == null) {
            adapter = new TaskRecycleListAdapter(this, cursor)
                    .setOnItemClickListener(new TaskRecycleListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClickListener(TaskViewHolder holder, boolean showDetail) {
                            onItemClicked(holder, showDetail);
                        }
                    });
        } else {
            adapter.updateCursor(cursor);
        }
    }

    public void initializeTaskList() {
        if (binding.taskList.getAdapter() == null) {
            binding.taskList.setLayoutManager(new LinearLayoutManager(this));
            binding.taskList.setAdapter(adapter);
        }
    }

    public void setActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(groupName);
        }
    }


    // handle widget clicks
    public void onItemClicked(final TaskViewHolder holder, boolean showDetail) {
        if (showDetail) {
            new AlertDialog.Builder(this)
                    .setTitle(holder.data.title)
                    .setMessage(holder.data.details)
                    .setPositiveButton(R.string.delete_task, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onDeleteTaskClicked(holder.data.id);
                        }
                    })
                    .setNeutralButton(R.string.close, null)
                    .setNegativeButton(R.string.edit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onEditTaskClicked(holder);
                        }
                    })
                    .setCancelable(true)
                    .show();
        } else {
            dbHelper.updateTaskCompletionState(db, holder.data.id, !holder.data.isCompleted);

            initializeCursor();
            initializeAdapter();
        }
    }

    public void onDeleteTaskClicked(int taskId) {
        dbHelper.deleteTask(db, taskId, groupName);

        initializeCursor();
        initializeAdapter();
    }

    public void onEditTaskClicked(TaskViewHolder holder) {
        Intent intent = new Intent(this, EditTaskActivity.class);
        intent.putExtra(TaskDbHelper.COLUMN_ID, holder.data.id);
        intent.putExtra(TaskDbHelper.COLUMN_TITLE, holder.data.title);
        intent.putExtra(TaskDbHelper.COLUMN_DETAIL, holder.data.details);
        intent.putExtra(TaskDbHelper.COLUMN_SHOW_COUNT, holder.data.showCount);
        intent.putExtra(TaskDbHelper.COLUMN_DAILY_TASK, holder.data.dailyTask);

        startActivityForResult(intent, Constants.ACTIVITY_EDIT_TASK);
    }


    // functionality
    public boolean sortOptionSelected(int itemId) {
        switch (itemId) {
            case R.id.atozOption:
                PrefManager.setSortType(this, Constants.SORT_A_Z);
                break;
            case R.id.ztoaOption:
                PrefManager.setSortType(this, Constants.SORT_Z_A);
                break;

            case R.id.earlytooldOption:
                PrefManager.setSortType(this, Constants.SORT_EARLY_OLD);
                break;
            case R.id.oldtoearlyOption:
                PrefManager.setSortType(this, Constants.SORT_OLD_EARLY);
                break;

            case R.id.completedtopendingOption:
                PrefManager.setSortType(this, Constants.SORT_COMPLETE_PENDING);
                break;
            case R.id.pendingtocompletedOption:
                PrefManager.setSortType(this, Constants.SORT_PENDING_COMPLETE);
                break;

            default:
                return false;
        }

        initializeCursor();
        initializeAdapter();
        initializeTaskList();
        return true;
    }

}