// DashboardFragment.kt
package com.whoisridze.moodtracker.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.whoisridze.moodtracker.R

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<CalendarView>(R.id.calendarView)
            .setOnDateChangeListener { _, year, month, day ->
                val bundle = Bundle().apply {
                    putInt("year", year)
                    putInt("month", month)
                    putInt("day", day)
                }
                findNavController().navigate(R.id.logMoodFragment, bundle)
            }
    }
}
