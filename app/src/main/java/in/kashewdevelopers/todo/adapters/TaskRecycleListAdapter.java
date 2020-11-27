package in.kashewdevelopers.todo.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import in.kashewdevelopers.todo.R;
import in.kashewdevelopers.todo.data_container.TaskDataContainer;

public class TaskRecycleListAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    private Cursor cursor;
    private Context context;

    public TaskRecycleListAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        this.context = context;
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_item_layout, parent, false);

        return new TaskViewHolder(view)
                .setOnItemClickListener(new TaskViewHolder.OnItemClickListener() {
                    @Override
                    public void onItemClickListener(TaskViewHolder holder, boolean showDetail) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClickListener(holder, showDetail);
                        }
                    }
                });
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        holder.data = new TaskDataContainer(cursor);

        holder.binding.checkbox.setChecked(holder.data.isCompleted);
        holder.binding.title.setText(holder.data.title);

        if (holder.data.details.length() < 1) {
            holder.binding.detail.setText("");
            holder.binding.detail.setVisibility(View.GONE);
        } else {
            holder.binding.detail.setText(holder.data.details);
            holder.binding.detail.setVisibility(View.VISIBLE);
        }

        if (holder.data.showCount) {
            long currentTime = System.currentTimeMillis();
            int totalDays = (int) Math.ceil((currentTime - holder.data.taskCreationTime) / 1000.0 / 60 / 60 / 24);
            holder.binding.taskCountStat.setText(context.getString(R.string.task_count_stat, holder.data.count, totalDays));
            holder.binding.taskCountStat.setVisibility(View.VISIBLE);
        } else {
            holder.binding.taskCountStat.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    public void updateCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }


    // callbacks
    public interface OnItemClickListener {
        void onItemClickListener(TaskViewHolder holder, boolean showDetail);
    }

    private OnItemClickListener onItemClickListener;

    public TaskRecycleListAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }
}
