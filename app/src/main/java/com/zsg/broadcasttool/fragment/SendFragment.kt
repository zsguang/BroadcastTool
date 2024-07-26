package com.zsg.broadcasttool.fragment

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zsg.broadcasttool.AppContext
import com.zsg.broadcasttool.R
import com.zsg.broadcasttool.adapter.HistoryAdapter
import com.zsg.broadcasttool.entity.ParamEntity
import com.zsg.broadcasttool.databinding.FragmentSendBinding
import com.zsg.broadcasttool.entity.BroadcastEntity
import com.zsg.broadcasttool.utils.LogUtil
import com.zsg.broadcasttool.utils.SPUtils
import com.zsg.broadcasttool.utils.ToastUtil


class SendFragment : Fragment() {
    private val TAG = "SendFragment"
    private var _binding: FragmentSendBinding? = null
    private val binding get() = _binding!!
    private val gson = Gson()

    private val typeViewList: MutableList<View> = mutableListOf()
    private val keyViewList: MutableList<View> = mutableListOf()
    private val valueViewList: MutableList<View> = mutableListOf()

    private val broadcastList: MutableList<BroadcastEntity> by lazy {
        LogUtil.i(
            TAG,
            "KEY_HISTORY_BROADCASTS: ${SPUtils.getInstance(AppContext.context).getSPString(AppContext.KEY_HISTORY_BROADCASTS, "[]")} "
        )
        gson.fromJson(
            SPUtils.getInstance(AppContext.context).getSPString(AppContext.KEY_HISTORY_BROADCASTS, "[]"),
            object : TypeToken<List<BroadcastEntity>>() {}.type
        )
    }
    private lateinit var historyAdapter: HistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSendBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        historyAdapter = HistoryAdapter(broadcastList)
        historyAdapter.setOnItemClickListener(object : HistoryAdapter.OnItemClickListener {
            override fun onItemClick(broadcastEntity: BroadcastEntity) {
                LogUtil.i(TAG, "onItemClick: $broadcastEntity")
                binding.etAction.setText(broadcastEntity.action)
                binding.etPackage.setText(broadcastEntity.packageName)
                typeViewList.clear()
                keyViewList.clear()
                valueViewList.clear()
                binding.extraParamView.removeAllViews()
                for (i in 0 until broadcastEntity.params.size) {
                    addView(broadcastEntity.params[i].type, broadcastEntity.params[i].key, broadcastEntity.params[i].value)
                }
            }
        })
        binding.rvHistory.layoutManager = LinearLayoutManager(activity)
        binding.rvHistory.adapter = historyAdapter

        binding.llParamTitle.setOnClickListener {
            binding.extraParamView.visibility = if (binding.extraParamView.visibility == View.VISIBLE) {
                binding.ivParamFold.setImageResource(R.drawable.fold)
                View.GONE
            } else {
                binding.ivParamFold.setImageResource(R.drawable.unfold)
                View.VISIBLE
            }
        }

        binding.btnAddParam.setOnClickListener {
            binding.extraParamView.visibility = View.VISIBLE
            binding.ivParamFold.setImageResource(R.drawable.unfold)
            addView(ParamEntity.ParamType.STRING, "", "")
        }

        binding.btnSendBroadcast.setOnClickListener {
            var flag = true
            val action = binding.etAction.text.toString().trim()
            val packageName = binding.etPackage.text.toString().trim()
            val params = mutableListOf<ParamEntity>()
            val intent = Intent()
            if (action.isEmpty()) {
                binding.etAction.error = getString(R.string.msg_action_empty)
                flag = false
            }
            if (packageName.isNotEmpty()) {
                intent.setPackage(packageName)
            }
            intent.action = action
            for (i in 0 until typeViewList.size) {
                val type = (typeViewList[i] as Spinner).selectedItem.toString().uppercase()
                val key = (keyViewList[i] as EditText).text.toString().trim()
                val value = (valueViewList[i] as EditText).text.toString().trim()

                if (key.isEmpty()) {
                    (keyViewList[i] as EditText).error = getString(R.string.msg_key_empty)
                    flag = false
                }
                if (value.isEmpty()) {
                    (valueViewList[i] as EditText).error = getString(R.string.msg_value_empty)
                    flag = false
                }
                try {
                    when (ParamEntity.ParamType.valueOf(type)) {
                        ParamEntity.ParamType.INT -> intent.putExtra(key, value.toInt())
                        ParamEntity.ParamType.LONG -> intent.putExtra(key, value.toLong())
                        ParamEntity.ParamType.FLOAT -> intent.putExtra(key, value.toFloat())
                        ParamEntity.ParamType.DOUBLE -> intent.putExtra(key, value.toDouble())
                        ParamEntity.ParamType.BOOLEAN -> {
                            if (value.lowercase() != "true" && value.lowercase() != "false") {
                                throw Exception("value type error")
                            }
                            intent.putExtra(key, value.toBoolean())
                        }
                        ParamEntity.ParamType.INT_ARRAY -> {
                            val intList = value.split(" ").mapNotNull { it.toIntOrNull() }
                            if (intList.size == value.split(" ").size) {
                                intent.putExtra(key, intList.toIntArray())
                            } else {
                                (valueViewList[i] as EditText).error = getString(R.string.msg_value_error)
                                flag = false
                            }
                            intent.putExtra(key, intList.toIntArray())
                        }

                        else -> intent.putExtra(key, value)
                    }
                } catch (e: Exception) {
                    (valueViewList[i] as EditText).error = getString(R.string.msg_value_type_error)
                    flag = false
                }
                params.add(ParamEntity(ParamEntity.ParamType.valueOf(type), key, value))
            }
            if (flag) {
                AppContext.context.sendBroadcast(intent)
                val broadcastEntity = BroadcastEntity(action, packageName, params)
                historyAdapter.addHistory(broadcastEntity)
                binding.rvHistory.scrollToPosition(0)

                SPUtils.getInstance(AppContext.context).setSPString(
                    AppContext.KEY_HISTORY_BROADCASTS,
                    gson.toJson(historyAdapter.getBroadcastList())
                )

                LogUtil.i(TAG, "send broadcast: $broadcastEntity")
                ToastUtil.showSuccess(getString(R.string.msg_send_broadcast_success))
            } else {
                ToastUtil.showError(getString(R.string.msg_param_error))
            }
        }
    }


    private fun addView(type: ParamEntity.ParamType, key: String, value: String) {
        val inflater = LayoutInflater.from(AppContext.context)
        val itemView: View = inflater.inflate(R.layout.item_param, null)
        val spType = itemView.findViewById<Spinner>(R.id.spType)
        val etKey = itemView.findViewById<EditText>(R.id.etKey)
        val etValue = itemView.findViewById<EditText>(R.id.etValue)
        val ivDelete = itemView.findViewById<View>(R.id.ivDelete)

        typeViewList.add(spType)
        keyViewList.add(etKey)
        valueViewList.add(etValue)
        ivDelete.setOnClickListener {
            binding.extraParamView.removeView(itemView)
            typeViewList.remove(spType)
            keyViewList.remove(etKey)
            valueViewList.remove(etValue)
        }
        spType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                /*
                <item>String</item>
                <item>Int</item>
                <item>Long</item>
                <item>Float</item>
                <item>Double</item>
                <item>Boolean</item>
                <item>IntArray</item>
                */
                if (position == ParamEntity.ParamType.BOOLEAN.ordinal) {
                    // Boolean类型，取消键盘输入，改为点击选择true/false
                    etValue.isFocusable = false
                    etValue.isFocusableInTouchMode = false
                    if (etValue.text.toString() != "true" && etValue.text.toString() != "false") {
                        etValue.setText("")
                    }
                    etValue.setOnClickListener { v: View ->
                        val popupMenu = PopupMenu(activity!!, v, Gravity.START)
                        popupMenu.menuInflater.inflate(R.menu.item_true_false, popupMenu.menu)
                        popupMenu.setOnMenuItemClickListener { it: MenuItem ->
                            if (it.itemId == R.id.item_true) {
                                etValue.setText("true")
                            } else if (it.itemId == R.id.item_false) {
                                etValue.setText("false")
                            }
                            true
                        }
                        popupMenu.show()
                    }
                } else {
                    etValue.isFocusable = true
                    etValue.isFocusableInTouchMode = true
                    etValue.setOnClickListener { }
                }
                when (ParamEntity.ParamType.values()[position]) {
                    ParamEntity.ParamType.STRING -> etValue.inputType = InputType.TYPE_CLASS_TEXT
                    ParamEntity.ParamType.INT -> etValue.inputType = InputType.TYPE_CLASS_NUMBER
                    ParamEntity.ParamType.LONG -> etValue.inputType = InputType.TYPE_CLASS_NUMBER
                    ParamEntity.ParamType.FLOAT -> etValue.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    ParamEntity.ParamType.DOUBLE -> etValue.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    ParamEntity.ParamType.BOOLEAN -> etValue.inputType = InputType.TYPE_NULL
                    ParamEntity.ParamType.INT_ARRAY -> etValue.inputType = InputType.TYPE_CLASS_TEXT
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        spType.setSelection(type.ordinal)
        etKey.setText(key)
        etValue.setText(value)

        binding.extraParamView.addView(itemView)
    }

    fun clearAll() {
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.msg_confirm_clear_all_send_history))
            .setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                historyAdapter.clearAll()
                SPUtils.getInstance(AppContext.context).setSPString(AppContext.KEY_HISTORY_BROADCASTS, "[]")
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

}