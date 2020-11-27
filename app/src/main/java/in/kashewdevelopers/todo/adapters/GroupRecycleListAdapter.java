package in.kashewdevelopers.todo.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import in.kashewdevelopers.todo.R;
import in.kashewdevelopers.todo.db.GroupDbHelper;

public class GroupRecycleListAdapter extends RecyclerView.Adapter<GroupViewHolder> {

    private Cursor cursor;
    private Context context;

    public GroupRecycleListAdapter(Cursor cursor, Context context) {
        this.cursor = cursor;
        this.context = context;
    }


    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_list_item_layout, parent, false);

        return new GroupViewHolder(view)
                .setOnItemClickListener(new GroupViewHolder.OnItemClickListener() {
                    @Override
                    public void onItemClickListener(GroupViewHolder holder, boolean showDetail) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClickListener(holder, showDetail);
                        }
                    }
                });
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        holder.groupName = cursor.getString(cursor.getColumnIndex(GroupDbHelper.COLUMN_TITLE));
        int count = cursor.getInt(cursor.getColumnIndex(GroupDbHelper.COLUMN_COUNT));

        holder.binding.groupName.setText(holder.groupName);
        holder.binding.taskCount.setText(context.getString(R.string.task_count, count,
                context.getResources().getQuantityString(R.plurals.task_quantity, count)));
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
        void onItemClickListener(GroupViewHolder holder, boolean showDetail);
    }

    private OnItemClickListener onItemClickListener;

    public GroupRecycleListAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }
}
