package com.zsg.broadcasttool.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zsg.broadcasttool.R
import com.zsg.broadcasttool.utils.LogUtil

class ReceiverAdapter : RecyclerView.Adapter<ReceiverAdapter.ReceiverViewHolder>() {
    private val receiverList: MutableList<ReceiverEntity> = mutableListOf()
    private var selectIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiverViewHolder {
        val view = View.inflate(parent.context, R.layout.item_receiver, null)
        return ReceiverViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiverViewHolder, position: Int) {
        holder.tvAction.text = receiverList[position].action
        holder.tvParams.text = receiverList[position].params
        holder.tvParams.visibility = if (position == selectIndex) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener {
            val lastSelectIndex = selectIndex
            selectIndex = if (holder.adapterPosition == selectIndex) -1 else holder.adapterPosition
            notifyItemChanged(lastSelectIndex)
            notifyItemChanged(selectIndex)
        }
        holder.itemView.isSelected = position == selectIndex
    }

    override fun getItemCount(): Int {
        return receiverList.size
    }

    fun clearAll() {
        selectIndex = -1
        receiverList.clear()
        notifyDataSetChanged()
    }

    fun addReceiver(action: String, params: String) {
        LogUtil.i("TAG", "$action  $params")
        if (selectIndex >= 0) selectIndex++
        receiverList.add(0, ReceiverEntity(action, params))
        notifyItemInserted(0)
    }


    class ReceiverViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAction: TextView = view.findViewById(R.id.tvAction)
        val tvParams: TextView = view.findViewById(R.id.tvParams)
    }

    internal data class ReceiverEntity(val action: String, val params: String)
}