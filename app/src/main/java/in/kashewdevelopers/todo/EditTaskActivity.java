package in.kashewdevelopers.todo;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import in.kashewdevelopers.todo.databinding.ActivityAddTaskBinding;
import in.kashewdevelopers.todo.db.TaskDbHelper;

public class EditTaskActivity extends AppCompatActivity {

    ActivityAddTaskBinding binding;

    private SQLiteDatabase db;
    private TaskDbHelper dbHelper;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialize();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // initialization
    public void initialize() {
        dbHelper = new TaskDbHelper(this);
        db = dbHelper.getWritableDatabase();

        binding.addTask.setText(R.string.update);

        setBackButton();
        setData();
    }

    public void setBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setData() {
        Intent intent = getIntent();

        id = intent.getIntExtra(TaskDbHelper.COLUMN_ID, -1);

        binding.taskTitle.setText(intent.getStringExtra(TaskDbHelper.COLUMN_TITLE));
        binding.taskDetails.setText(intent.getStringExtra(TaskDbHelper.COLUMN_DETAIL));
        binding.dailyTask.setChecked(intent.getBooleanExtra(TaskDbHelper.COLUMN_DAILY_TASK, false));
        binding.showCount.setChecked(intent.getBooleanExtra(TaskDbHelper.COLUMN_SHOW_COUNT, false));

        onDailyTaskClicked(null);
    }


    // handle widget clicks
    public void onAddClicked(View view) {
        if (!verifyTitle()) {
            return;
        }

        dbHelper.updateTask(db, id, binding.taskTitle.getText().toString(),
                binding.taskDetails.getText().toString(),
                binding.dailyTask.isChecked(), binding.showCount.isChecked());
        dbHelper.close();

        Toast taskUpdated = Toast.makeText(this, R.string.task_updated, Toast.LENGTH_LONG);
        taskUpdated.setGravity(Gravity.CENTER, 0, 0);
        taskUpdated.show();

        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void onDailyTaskClicked(View view) {
        if (binding.dailyTask.isChecked()) {
            binding.showCount.setVisibility(View.VISIBLE);
        } else {
            binding.showCount.setChecked(false);
            binding.showCount.setVisibility(View.GONE);
        }
    }


    // functionality
    public boolean verifyTitle() {
        if (binding.taskTitle.length() < 1) {
            binding.taskTitle.setError(getText(R.string.cannot_be_empty));
            binding.taskTitle.requestFocus();
            return false;
        }

        return true;
    }

}