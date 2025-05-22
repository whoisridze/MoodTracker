package com.whoisridze.moodtracker.ui.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whoisridze.moodtracker.domain.model.MoodEntry
import com.whoisridze.moodtracker.domain.model.MoodValue
import com.whoisridze.moodtracker.domain.repository.MoodRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class StatsViewModel(private val repository: MoodRepository) : ViewModel() {

    enum class StatsPeriod {
        WEEK, MONTH, YEAR, ALL_TIME
    }

    private val _selectedPeriod = MutableLiveData<StatsPeriod>()
    val selectedPeriod: LiveData<StatsPeriod> = _selectedPeriod

    private val _moodDistribution = MutableLiveData<Map<MoodValue, Int>>()
    val moodDistribution: LiveData<Map<MoodValue, Int>> = _moodDistribution

    private val _moodTimeline = MutableLiveData<Map<LocalDate, MoodValue>>()
    val moodTimeline: LiveData<Map<LocalDate, MoodValue>> = _moodTimeline

    private val _mostFrequentMood = MutableLiveData<MoodValue?>()
    val mostFrequentMood: LiveData<MoodValue?> = _mostFrequentMood

    private val _averageMood = MutableLiveData<Double>()
    val averageMood: LiveData<Double> = _averageMood

    private val _currentStreak = MutableLiveData<Int>()
    val currentStreak: LiveData<Int> = _currentStreak

    private val _bestStreak = MutableLiveData<Int>()
    val bestStreak: LiveData<Int> = _bestStreak

    private val _totalEntries = MutableLiveData<Int>()
    val totalEntries: LiveData<Int> = _totalEntries

    init {
        setPeriod(StatsPeriod.WEEK)
    }

    fun setPeriod(period: StatsPeriod) {
        if (period == _selectedPeriod.value) return

        _selectedPeriod.value = period
        loadStats()
    }

    fun refreshData() {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val allEntries = repository.getMoods()
            val filteredEntries = filterEntriesByPeriod(allEntries)

            calculateMoodDistribution(filteredEntries)
            createMoodTimeline(filteredEntries)
            findMostFrequentMood(filteredEntries)
            calculateAverageMood(filteredEntries)
            calculateStreaks(allEntries)
            _totalEntries.value = filteredEntries.size
        }
    }

    private fun filterEntriesByPeriod(entries: List<MoodEntry>): List<MoodEntry> {
        val today = LocalDate.now()
        return when (_selectedPeriod.value) {
            StatsPeriod.WEEK -> entries.filter { ChronoUnit.DAYS.between(it.date, today) < 7 }
            StatsPeriod.MONTH -> entries.filter { ChronoUnit.DAYS.between(it.date, today) < 30 }
            StatsPeriod.YEAR -> entries.filter { it.date.year == today.year }
            StatsPeriod.ALL_TIME, null -> entries
        }
    }

    private fun calculateMoodDistribution(entries: List<MoodEntry>) {
        val distribution = MoodValue.values().associateWith { mood ->
            entries.count { it.moodType == mood }
        }
        _moodDistribution.value = distribution
    }

    private fun createMoodTimeline(entries: List<MoodEntry>) {
        val timeline = entries.groupBy { it.date }
            .mapValues { (_, entriesForDay) ->
                entriesForDay.maxByOrNull { it.time }?.moodType ?: MoodValue.NEUTRAL
            }
        _moodTimeline.value = timeline
    }

    private fun findMostFrequentMood(entries: List<MoodEntry>) {
        if (entries.isEmpty()) {
            _mostFrequentMood.value = null
            return
        }

        val moodCounts = entries.groupingBy { it.moodType }.eachCount()
        _mostFrequentMood.value = moodCounts.maxByOrNull { it.value }?.key
    }

    private fun calculateAverageMood(entries: List<MoodEntry>) {
        if (entries.isEmpty()) {
            _averageMood.value = 0.0
            return
        }

        val total = entries.sumOf { entry ->
            getMoodScore(entry.moodType)
        }
        _averageMood.value = total.toDouble() / entries.size
    }

    private fun getMoodScore(mood: MoodValue): Int {
        return when (mood) {
            MoodValue.AWFUL -> 1
            MoodValue.BAD -> 2
            MoodValue.NEUTRAL -> 3
            MoodValue.GOOD -> 4
            MoodValue.GREAT -> 5
        }
    }

    private fun calculateStreaks(entries: List<MoodEntry>) {
        if (entries.isEmpty()) {
            _currentStreak.value = 0
            _bestStreak.value = 0
            return
        }

        val moodDates = entries.map { it.date }.toSet()

        var currentStreak = 0
        var date = LocalDate.now()

        while (moodDates.contains(date)) {
            currentStreak++
            date = date.minusDays(1)
        }
        _currentStreak.value = currentStreak

        val sortedDates = entries.map { it.date }.sorted().distinct()
        var bestStreak = 0
        var tempStreak = 1

        for (i in 1 until sortedDates.size) {
            val daysBetween = ChronoUnit.DAYS.between(sortedDates[i - 1], sortedDates[i])
            if (daysBetween == 1L) {
                tempStreak++
            } else {
                bestStreak = maxOf(bestStreak, tempStreak)
                tempStreak = 1
            }
        }
        bestStreak = maxOf(bestStreak, tempStreak)

        _bestStreak.value = bestStreak
    }
}