package com.zsg.broadcasttool.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.zsg.broadcasttool.AppContext
import com.zsg.broadcasttool.R
import com.zsg.broadcasttool.fragment.ReceiverFragment
import com.zsg.broadcasttool.fragment.SendFragment

class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private var tabTitles: List<String> =
        listOf(
            AppContext.context.getString(R.string.send),
            AppContext.context.getString(R.string.receive)
        )
    private var fragmentList: List<Fragment> = listOf(
        SendFragment(),
        ReceiverFragment(),
    )

    override fun getCount(): Int {
        return tabTitles.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }

    fun clearAll(position: Int) {
        if (position == 0) {
            val sendFragment = fragmentList[position] as SendFragment
            sendFragment.clearAll()
        } else if (position == 1) {
            val receiverFragment = fragmentList[position] as ReceiverFragment
            receiverFragment.clearAll()
        }
    }
}