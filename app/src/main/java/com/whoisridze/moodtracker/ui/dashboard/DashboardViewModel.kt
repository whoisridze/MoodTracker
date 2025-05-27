package com.whoisridze.moodtracker.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whoisridze.moodtracker.domain.repository.MoodRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardViewModel(private val repository: MoodRepository) : ViewModel() {

    private val _moodDates = MutableLiveData<Set<LocalDate>>()
    val moodDates: LiveData<Set<LocalDate>> = _moodDates

    init {
        loadMoodDates()
    }

    private fun loadMoodDates() {
        viewModelScope.launch {
            val entries = repository.getMoods()
            val dates = entries.map { it.date }.toSet()
            _moodDates.postValue(dates)
        }
    }

    fun refreshData() {
        loadMoodDates()
    }
}