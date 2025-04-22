package com.whoisridze.moodtracker.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.whoisridze.moodtracker.R

class LogMoodFragment : Fragment(R.layout.fragment_log_mood) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val year = requireArguments().getInt("year")
        val month = requireArguments().getInt("month")
        val day = requireArguments().getInt("day")
    }
}
