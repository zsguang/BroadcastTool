package com.zsg.broadcasttool.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zsg.broadcasttool.AppContext
import com.zsg.broadcasttool.R
import com.zsg.broadcasttool.adapter.ReceiverAdapter
import com.zsg.broadcasttool.databinding.FragmentReceiverBinding
import com.zsg.broadcasttool.utils.SPUtils
import com.zsg.broadcasttool.utils.ToastUtil

class ReceiverFragment : Fragment() {
    private val TAG = "RegisterFragment"
    private var _binding: FragmentReceiverBinding? = null
    private val binding get() = _binding!!
    private val gson = Gson()
    private val handler = Handler(Looper.getMainLooper())

    private val registerList: MutableList<BroadcastReceiver> = mutableListOf() // 记录注册的广播接收器
    private val registerStateMap = mutableMapOf<BroadcastReceiver, Boolean>() // 记录广播接收器的注册状态
    private val actionList = mutableListOf<String>()    // 已注册的广播的action
    private lateinit var receiverAdapter: ReceiverAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReceiverBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        binding.llRegister.setOnClickListener {
            binding.llRegisterView.visibility = if (binding.llRegisterView.visibility == View.VISIBLE) {
                binding.ivRegisterFold.setImageResource(R.drawable.rect_fold)
                View.GONE
            } else {
                binding.ivRegisterFold.setImageResource(R.drawable.rect_unfold)
                View.VISIBLE
            }
        }
        binding.btnRegister.setOnClickListener {
            val action = binding.etAction.text.toString().trim()
            if (action.isEmpty()) {
                binding.etAction.error = getString(R.string.msg_action_empty)
                return@setOnClickListener
            }
            addRegister(action, true)
        }

        binding.rvReceive.layoutManager = LinearLayoutManager(activity)
        receiverAdapter = ReceiverAdapter()
        binding.rvReceive.adapter = receiverAdapter

        val actions: List<String> = gson.fromJson(
            SPUtils.getInstance(AppContext.context).getSPString(AppContext.KEY_HISTORY_RECEIVES, "[]"),
            object : TypeToken<List<String>>() {}.type
        )
        for (action in actions) {
            addRegister(action, false)
        }
    }


    fun clearAll() {
        receiverAdapter.clearAll()
    }

    private fun addRegister(action: String, isRegister: Boolean) {
        if (action == "") return
        if (actionList.contains(action)) {
            ToastUtil.showError(getString(R.string.msg_action_exist))
            return
        }
        actionList.add(action)

        val view = LayoutInflater.from(activity).inflate(R.layout.item_register, null)
        val tvAction = view.findViewById<View>(R.id.tvAction) as TextView
        val ivRegister = view.findViewById<View>(R.id.ivRegister) as ImageView
        val ivDelete = view.findViewById<View>(R.id.ivDelete) as ImageView

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                var text = ""
                if (intent == null || intent.action == null) return
                if (intent.extras != null) {
                    intent.extras?.keySet()?.forEach {
                        text += "\"" + it + "\" : " + intent.extras?.get(it) + "\n"
                    }
                }
                handler.post {
                    binding.rvReceive.scrollToPosition(0)
                    receiverAdapter.addReceiver(intent.action!!, text.trimEnd())
                }

            }
        }
        if (isRegister) {
            activity?.registerReceiver(receiver, IntentFilter(action))
            registerStateMap[receiver] = true
            ivRegister.setImageResource(R.drawable.stop)
            tvAction.setTextColor(AppContext.context.getColor(R.color.font_green))
        }
        ivRegister.setOnClickListener {
            if (registerStateMap[receiver] == true) {
                activity?.unregisterReceiver(receiver)
                registerStateMap[receiver] = false
                ivRegister.setImageResource(R.drawable.run)
                tvAction.setTextColor(AppContext.context.getColor(R.color.font_black))
            } else {
                activity?.registerReceiver(receiver, IntentFilter(action))
                registerStateMap[receiver] = true
                ivRegister.setImageResource(R.drawable.stop)
                tvAction.setTextColor(AppContext.context.getColor(R.color.font_green))
            }
        }
        ivDelete.setOnClickListener {
            if (registerStateMap[receiver] == true) {
                activity?.unregisterReceiver(receiver)
            }
            actionList.remove(action)
            registerList.remove(receiver)
            registerStateMap.remove(receiver)
            binding.llRegisterList.removeView(view)
            SPUtils.getInstance(activity).setSPString(AppContext.KEY_HISTORY_RECEIVES, gson.toJson(actionList))
        }
        tvAction.text = action
        binding.llRegisterList.addView(view)
        SPUtils.getInstance(activity).setSPString(AppContext.KEY_HISTORY_RECEIVES, gson.toJson(actionList))
    }


}