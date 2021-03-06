package `in`.kashewdevelopers.todo.adapters

import `in`.kashewdevelopers.todo.R
import `in`.kashewdevelopers.todo.data_container.TaskDataContainer
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TaskRecycleListAdapter(private var cursor: Cursor, private var context: Context) : RecyclerView.Adapter<TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.task_list_item_layout, parent, false)

        return TaskViewHolder(view)
                .setOnItemClickListener(object : TaskViewHolder.OnItemClickListener {
                    override fun onItemClickListener(holder: TaskViewHolder, showDetail: Boolean) {
                        onItemClickListener?.onItemClickListener(holder, showDetail)
                    }
                })
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        if (!cursor.moveToPosition(position)) {
            return
        }

        holder.data = TaskDataContainer(cursor)

        holder.binding.checkbox.isChecked = holder.data.isCompleted
        holder.binding.title.text = holder.data.title

        holder.binding.detail.text = holder.data.details
        holder.binding.detail.visibility = if (holder.data.details.isEmpty()) View.GONE else View.VISIBLE

        if (holder.data.showCount) {
            holder.binding.taskCountStat.text = context.getString(R.string.task_count_stat, holder.data.count, holder.data.total)
            holder.binding.taskCountStat.visibility = View.VISIBLE
        } else {
            holder.binding.taskCountStat.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = cursor.count

    fun updateCursor(cursor: Cursor) {
        this.cursor = cursor
        notifyDataSetChanged()
    }


    // callbacks
    interface OnItemClickListener {
        fun onItemClickListener(holder: TaskViewHolder, showDetail: Boolean)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?): TaskRecycleListAdapter {
        this.onItemClickListener = onItemClickListener
        return this
    }
}
