package com.zsg.broadcasttool.adapter

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.zsg.broadcasttool.AppContext
import com.zsg.broadcasttool.R
import com.zsg.broadcasttool.entity.BroadcastEntity
import com.zsg.broadcasttool.entity.ParamEntity
import com.zsg.broadcasttool.utils.LogUtil
import com.zsg.broadcasttool.utils.SPUtils
import java.util.Locale

class HistoryAdapter(private var broadcastList: MutableList<BroadcastEntity>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null
    private var selectIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_broadcast, parent, false)
        return HistoryViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.tvAction.text = broadcastList[position].action
        holder.tvPackage.text = broadcastList[position].packageName.apply { this.ifEmpty { "null" } }

        val paramStr = broadcastList[position].params.joinToString("\n") { param ->
            val typeStr = param.type.name.padEnd(8).lowercase().capitalize(Locale.getDefault())
            val valueStr = if (param.type == ParamEntity.ParamType.STRING) "\"${param.value}\"" else param.value
            "$typeStr\"${param.key}\":$valueStr"
        }
        holder.tvParams.text = paramStr
        // holder.tvParams.visibility = if (broadcastList[holder.adapterPosition].isFold) View.GONE else View.VISIBLE
        holder.tvParams.visibility = if (position == selectIndex) View.VISIBLE else View.GONE

        holder.itemView.isSelected = position == selectIndex
        holder.itemView.setOnClickListener {
            mOnItemClickListener?.onItemClick(broadcastList[holder.adapterPosition])
            // broadcastList[holder.adapterPosition].isFold = !broadcastList[holder.adapterPosition].isFold

            val lastSelectIndex = selectIndex
            selectIndex = holder.adapterPosition
            notifyItemChanged(lastSelectIndex)
            notifyItemChanged(selectIndex)
        }
        holder.itemView.setOnLongClickListener {
            LogUtil.i("HistoryAdapter", "onLongClick")
            val popupMenu = PopupMenu(it.context, it, Gravity.CENTER)
            popupMenu.menuInflater.inflate(R.menu.item_delete, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                // 删除该项
                broadcastList.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
                SPUtils.getInstance(AppContext.context).setSPString(AppContext.KEY_HISTORY_BROADCASTS, Gson().toJson(broadcastList))
                true
            }
            popupMenu.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return broadcastList.size
    }

    fun clearAll() {
        selectIndex = -1
        broadcastList.clear()
        notifyDataSetChanged()
    }

    fun addHistory(broadcastEntity: BroadcastEntity) {
        var exitEntity: BroadcastEntity? = null
        for (entity: BroadcastEntity in broadcastList) {
            if (entity == broadcastEntity) {
                exitEntity = entity
                break
            }
        }
        if (exitEntity != null) {
            val index = broadcastList.indexOf(exitEntity)
            broadcastList.remove(exitEntity)
            broadcastList.add(0, exitEntity)
            selectIndex = 0
            notifyItemMoved(index, 0)
            notifyItemChanged(index)
            notifyItemChanged(0)
        } else {
            broadcastList.add(0, broadcastEntity)
            val lastSelectIndex = selectIndex
            selectIndex = 0
            notifyItemChanged(lastSelectIndex)
            notifyItemInserted(0)
        }
    }

    fun getBroadcastList(): MutableList<BroadcastEntity> {
        return broadcastList
    }


    interface OnItemClickListener {
        fun onItemClick(broadcastEntity: BroadcastEntity)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvAction: TextView
        internal var tvPackage: TextView
        internal var tvParams: TextView

        init {
            tvAction = itemView.findViewById(R.id.tv_action)
            tvPackage = itemView.findViewById(R.id.tv_package)
            tvParams = itemView.findViewById(R.id.tv_params)
        }
    }

}