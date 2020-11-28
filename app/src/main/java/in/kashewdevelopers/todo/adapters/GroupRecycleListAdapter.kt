package `in`.kashewdevelopers.todo.adapters

import `in`.kashewdevelopers.todo.R
import `in`.kashewdevelopers.todo.db.GroupDbHelper
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GroupRecycleListAdapter(private var cursor: Cursor, private var context: Context) : RecyclerView.Adapter<GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.group_list_item_layout, parent, false)

        return GroupViewHolder(view)
                .setOnItemClickListener(object : GroupViewHolder.OnItemClickListener {
                    override fun onItemClickListener(holder: GroupViewHolder, showDetail: Boolean) {
                        onItemClickListener?.onItemClickListener(holder, showDetail)
                    }
                })
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        if (!cursor.moveToPosition(position)) {
            return
        }

        holder.groupName = cursor.getString(cursor.getColumnIndex(GroupDbHelper.COLUMN_TITLE))
        val count = cursor.getInt(cursor.getColumnIndex(GroupDbHelper.COLUMN_COUNT))

        holder.binding.groupName.text = holder.groupName
        holder.binding.taskCount.text = context.getString(R.string.task_count, count,
                context.resources.getQuantityString(R.plurals.task_quantity, count))
    }

    override fun getItemCount(): Int = cursor.count


    fun updateCursor(cursor: Cursor) {
        this.cursor = cursor
        notifyDataSetChanged()
    }


    // callbacks
    interface OnItemClickListener {
        fun onItemClickListener(holder: GroupViewHolder, showDetail: Boolean)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?): GroupRecycleListAdapter {
        this.onItemClickListener = onItemClickListener
        return this
    }
}
