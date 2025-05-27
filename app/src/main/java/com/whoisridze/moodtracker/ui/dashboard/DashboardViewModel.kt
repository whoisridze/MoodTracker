package com.whoisridze.moodtracker.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whoisridze.moodtracker.domain.model.MoodEntry
import com.whoisridze.moodtracker.domain.repository.MoodRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardViewModel(private val repository: MoodRepository) : ViewModel() {

    private val _moodDates = MutableLiveData<Set<LocalDate>>()
    val moodDates: LiveData<Set<LocalDate>> = _moodDates

    private val _morningPercentage = MutableLiveData<Int>()
    val morningPercentage: LiveData<Int> = _morningPercentage

    private val _afternoonPercentage = MutableLiveData<Int>()
    val afternoonPercentage: LiveData<Int> = _afternoonPercentage

    private val _eveningPercentage = MutableLiveData<Int>()
    val eveningPercentage: LiveData<Int> = _eveningPercentage

    init {
        loadMoodData()
    }

    private fun loadMoodData() {
        viewModelScope.launch {
            val entries = repository.getMoods()
            val dates = entries.map { it.date }.toSet()
            _moodDates.postValue(dates)

            calculateTimeOfDayPercentages(entries)
        }
    }

    private fun calculateTimeOfDayPercentages(entries: List<MoodEntry>) {
        if (entries.isEmpty()) {
            _morningPercentage.postValue(0)
            _afternoonPercentage.postValue(0)
            _eveningPercentage.postValue(0)
            return
        }

        var morningCount = 0
        var afternoonCount = 0
        var eveningCount = 0

        entries.forEach { entry ->
            when {
                entry.time.hour in 5..11 -> morningCount++
                entry.time.hour in 12..17 -> afternoonCount++
                else -> eveningCount++
            }
        }

        val total = entries.size
        _morningPercentage.postValue((morningCount * 100 / total))
        _afternoonPercentage.postValue((afternoonCount * 100 / total))
        _eveningPercentage.postValue((eveningCount * 100 / total))
    }

    fun refreshData() {
        loadMoodData()
    }
}