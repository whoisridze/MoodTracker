// LogMoodFragment.kt
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
        val tvDate = view.findViewById<TextView>(R.id.tvSelectedDate)
        val tvHappy = view.findViewById<TextView>(R.id.emojiHappy)
        val tvNeutral = view.findViewById<TextView>(R.id.emojiNeutral)
        val tvSad = view.findViewById<TextView>(R.id.emojiSad)
        val etNote = view.findViewById<EditText>(R.id.etNote)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val year = requireArguments().getInt("year")
        val month = requireArguments().getInt("month")
        val day = requireArguments().getInt("day")
        tvDate.text = "$day.${month + 1}.$year"
        btnSave.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
