package in.kashewdevelopers.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import in.kashewdevelopers.todo.adapters.GroupRecycleListAdapter;
import in.kashewdevelopers.todo.adapters.GroupViewHolder;
import in.kashewdevelopers.todo.databinding.ActivityGroupsBinding;
import in.kashewdevelopers.todo.databinding.AddGroupDialogBinding;
import in.kashewdevelopers.todo.databinding.EditGroupDialogBinding;
import in.kashewdevelopers.todo.db.GroupDbHelper;
import in.kashewdevelopers.todo.provider_classes.Constants;

public class GroupsActivity extends AppCompatActivity {

    ActivityGroupsBinding binding;

    private Cursor cursor;
    private SQLiteDatabase db;
    private GroupDbHelper dbHelper;
    private GroupRecycleListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addOption) {
            onAddGroupClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ACTIVITY_TASK_LIST) {
            initializeCursor();
            initializeAdapter();
            initializeList();
        }
    }


    // initialization
    public void initialize() {
        dbHelper = new GroupDbHelper(this);
        db = dbHelper.getReadableDatabase();

        initializeCursor();
        initializeAdapter();
        initializeList();
    }

    public void initializeCursor() {
        cursor = dbHelper.queryGroup(db);
        if (cursor.getCount() < 1) {
            binding.noGroups.setVisibility(View.VISIBLE);
        } else {
            binding.noGroups.setVisibility(View.GONE);
        }
    }

    public void initializeAdapter() {
        if (adapter == null) {
            adapter = new GroupRecycleListAdapter(cursor, this)
                    .setOnItemClickListener(new GroupRecycleListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClickListener(GroupViewHolder holder, boolean showDetail) {
                            onItemClicked(holder, showDetail);
                        }
                    });
        } else {
            adapter.updateCursor(cursor);
        }
    }

    public void initializeList() {
        if (binding.groupList.getAdapter() == null) {
            binding.groupList.setLayoutManager(new LinearLayoutManager(this));
            binding.groupList.setAdapter(adapter);
        }
    }


    // functionality
    public void addGroup(String groupName) {
        if (dbHelper.insertGroup(db, groupName)) {
            initializeCursor();
            initializeAdapter();
            initializeList();

            Toast groupCreated = Toast.makeText(this, R.string.group_added, Toast.LENGTH_LONG);
            groupCreated.setGravity(Gravity.CENTER, 0, 0);
            groupCreated.show();
        } else {
            Toast error = Toast.makeText(this, R.string.error_creating_group, Toast.LENGTH_LONG);
            error.setGravity(Gravity.CENTER, 0, 0);
            error.show();
        }
    }

    public void deleteGroup(String groupName) {
        dbHelper.deleteGroup(db, groupName);

        initializeCursor();
        initializeAdapter();
        initializeList();
    }

    public void updateGroup(String oldGroupName, String newGroupName) {
        dbHelper.updateGroup(db, oldGroupName, newGroupName);

        initializeCursor();
        initializeAdapter();
        initializeList();
    }


    // handle widget clicks
    public void onItemClicked(final GroupViewHolder holder, boolean showDetail) {
        if (showDetail) {
            new AlertDialog.Builder(this)
                    .setTitle(holder.groupName)
                    .setPositiveButton(R.string.delete_task, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onDeleteGroupClicked(holder.groupName);
                        }
                    })
                    .setNeutralButton(R.string.close, null)
                    .setNegativeButton(R.string.edit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onEditGroupClicked(holder.groupName);
                        }
                    })
                    .setCancelable(true)
                    .show();
        } else {
            Intent intent = new Intent(this, TasksActivity.class);
            intent.putExtra(Constants.GROUP, holder.groupName);
            startActivityForResult(intent, Constants.ACTIVITY_TASK_LIST);
        }
    }

    public void onDeleteGroupClicked(final String groupName) {
        new AlertDialog.Builder(this)
                .setTitle(groupName)
                .setMessage(R.string.delete_all_tasks_in_groups)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete_task, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGroup(groupName);
                    }
                })
                .show();
    }

    public void onEditGroupClicked(final String groupName) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();

        final EditGroupDialogBinding dialogBinding = EditGroupDialogBinding.inflate(getLayoutInflater());
        dialogBinding.groupName.setText(groupName);
        dialog.setView(dialogBinding.getRoot());

        dialog.setTitle(R.string.change_group_name);
        dialog.setCancelable(true);

        dialogBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogBinding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogBinding.groupName.length() < 1) {
                    dialogBinding.groupName.setError(getText(R.string.cannot_be_empty));
                    dialogBinding.groupName.requestFocus();
                    return;
                }

                if (groupName.equals(dialogBinding.groupName.getText().toString())) {
                    dialog.dismiss();
                    return;
                }

                updateGroup(groupName, dialogBinding.groupName.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void onAddGroupClicked() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();

        final AddGroupDialogBinding dialogBinding = AddGroupDialogBinding.inflate(getLayoutInflater());
        dialog.setView(dialogBinding.getRoot());

        dialog.setTitle(R.string.add_group);
        dialog.setCancelable(true);

        dialogBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogBinding.addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogBinding.groupName.length() < 1) {
                    dialogBinding.groupName.setError(getText(R.string.cannot_be_empty));
                    dialogBinding.groupName.requestFocus();
                    return;
                }
                addGroup(dialogBinding.groupName.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}