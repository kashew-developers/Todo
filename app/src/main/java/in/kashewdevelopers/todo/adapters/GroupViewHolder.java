package in.kashewdevelopers.todo.adapters;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import in.kashewdevelopers.todo.databinding.GroupListItemLayoutBinding;

public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public View view;
    public GroupListItemLayoutBinding binding;
    public String groupName;

    public GroupViewHolder(View view) {
        super(view);
        this.view = view;
        binding = GroupListItemLayoutBinding.bind(view);

        binding.root.setOnClickListener(this);
        binding.root.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
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
        void onItemClickListener(GroupViewHolder holder, boolean showDetail);
    }

    private OnItemClickListener onItemClickListener;

    public GroupViewHolder setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

}
