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

        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val bundle = Bundle().apply {
                putInt("selectedYear", year)
                putInt("selectedMonth", month)
                putInt("selectedDay", dayOfMonth)
            }
            findNavController().navigate(R.id.action_dashboardFragment_to_logMoodFragment, bundle)
        }
    }
}