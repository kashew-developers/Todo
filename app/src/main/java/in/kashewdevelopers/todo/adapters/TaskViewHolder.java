package in.kashewdevelopers.todo.adapters;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import in.kashewdevelopers.todo.data_container.TaskDataContainer;
import in.kashewdevelopers.todo.databinding.TaskListItemLayoutBinding;

public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public View view;
    public TaskListItemLayoutBinding binding;
    public TaskDataContainer data;

    public TaskViewHolder(View view) {
        super(view);
        this.view = view;
        binding = TaskListItemLayoutBinding.bind(view);

        binding.root.setOnClickListener(this);
        binding.root.setOnLongClickListener(this);

        binding.checkbox.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        view.setPressed(true);
        onClick(false);
    }

    @Override
    public boolean onLongClick(View v) {
        onClick(true);
        return true;
    }


    public void onClick(boolean showDetails) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClickListener(this, showDetails);
        }
    }


    // callbacks
    public interface OnItemClickListener {
        void onItemClickListener(TaskViewHolder holder, boolean showDetail);
    }

    private OnItemClickListener onItemClickListener;

    public TaskViewHolder setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

}
