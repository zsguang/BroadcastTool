package com.zsg.broadcasttool

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.zsg.broadcasttool.adapter.PagerAdapter
import com.zsg.broadcasttool.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var pagerAdapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        pagerAdapter = PagerAdapter(supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager.apply {
            offscreenPageLimit = 2
            adapter = pagerAdapter
        }
        binding.tabLayout.setupWithViewPager(viewPager)
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab: TabLayout.Tab? = binding.tabLayout.getTabAt(i)
            tab?.setCustomView(R.layout.item_tab)
            val textView: TextView = tab?.customView?.findViewById<View>(R.id.tab_title) as TextView
            textView.text = pagerAdapter.getPageTitle(i)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.allDelete -> {
                pagerAdapter.clearAll(binding.viewPager.currentItem)
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        // 收起键盘
        handler.postDelayed({
            val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }, 200)
    }

}