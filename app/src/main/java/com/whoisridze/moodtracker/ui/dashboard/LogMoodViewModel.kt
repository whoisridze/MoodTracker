package com.whoisridze.moodtracker.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whoisridze.moodtracker.domain.model.MoodEntry
import com.whoisridze.moodtracker.domain.model.MoodValue
import com.whoisridze.moodtracker.domain.repository.MoodRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class LogMoodViewModel(private val repository: MoodRepository) : ViewModel() {
    fun saveMood(date: LocalDate, time: LocalTime, moodType: MoodValue, reason: String?) {
        viewModelScope.launch {
            val moodEntry = MoodEntry(date, time, moodType, reason)
            repository.saveMood(moodEntry)
        }
    }
}