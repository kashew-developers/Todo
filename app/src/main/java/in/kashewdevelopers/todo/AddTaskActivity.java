package in.kashewdevelopers.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import in.kashewdevelopers.todo.databinding.ActivityAddTaskBinding;
import in.kashewdevelopers.todo.db.TaskDbHelper;
import in.kashewdevelopers.todo.provider_classes.Constants;

public class AddTaskActivity extends AppCompatActivity {

    ActivityAddTaskBinding binding;

    private SQLiteDatabase db;
    private TaskDbHelper dbHelper;

    private String groupName;


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
        Intent intent = getIntent();
        groupName = intent.getStringExtra(Constants.GROUP);

        dbHelper = new TaskDbHelper(this);
        db = dbHelper.getWritableDatabase();

        setBackButton();
    }

    public void setBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    // handle widget clicks
    public void onAddClicked(View view) {
        if (!verifyTitle()) {
            return;
        }

        dbHelper.insertTask(db, binding.taskTitle.getText().toString(),
                binding.taskDetails.getText().toString(), groupName,
                binding.dailyTask.isChecked(), binding.showCount.isChecked());
        dbHelper.close();

        Toast taskAdded = Toast.makeText(this, R.string.task_added, Toast.LENGTH_LONG);
        taskAdded.setGravity(Gravity.CENTER, 0, 0);
        taskAdded.show();

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