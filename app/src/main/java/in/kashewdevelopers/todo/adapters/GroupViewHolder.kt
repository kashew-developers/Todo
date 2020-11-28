package `in`.kashewdevelopers.todo.adapters

import `in`.kashewdevelopers.todo.databinding.GroupListItemLayoutBinding
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

    val binding = GroupListItemLayoutBinding.bind(view)
    var groupName: String? = null

    init {
        binding.root.setOnClickListener(this)
        binding.root.setOnLongClickListener(this)
    }

    override fun onClick(view: View) {
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
        fun onItemClickListener(holder: GroupViewHolder, showDetail: Boolean)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?): GroupViewHolder {
        this.onItemClickListener = onItemClickListener
        return this
    }

}
