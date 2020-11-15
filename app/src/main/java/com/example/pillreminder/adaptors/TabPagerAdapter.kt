package com.example.pillreminder.adaptors

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.pillreminder.fragments.FragmentDashboard
import com.example.pillreminder.fragments.FragmentReminder

class TabPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm){
    override fun getItem(p0: Int): Fragment {
        return when (p0) {
            0 -> {
                FragmentDashboard()
            }
            1 -> FragmentReminder()
            else -> {
                return FragmentReminder()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Dashboard"
            1 -> "Reminder"
            else -> {
                return "Rating"
            }
        }
    }
}