package `in`.kashewdevelopers.todo.adapters

import `in`.kashewdevelopers.todo.data_container.TaskDataContainer
import `in`.kashewdevelopers.todo.databinding.TaskListItemLayoutBinding
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

    val binding = TaskListItemLayoutBinding.bind(view)
    lateinit var data: TaskDataContainer

    init {
        binding.root.setOnClickListener(this)
        binding.root.setOnLongClickListener(this)

        binding.checkbox.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        view.isPressed = true
        onClick(false)
    }

    override fun onLongClick(view: View): Boolean {
        onClick(true)
        return true
    }

    private fun onClick(showDetails: Boolean) {
        onItemClickListener?.onItemClickListener(this, showDetails)
    }


    // callbacks
    interface OnItemClickListener {
        fun onItemClickListener(holder: TaskViewHolder, showDetail: Boolean)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?): TaskViewHolder {
        this.onItemClickListener = onItemClickListener
        return this
    }

}