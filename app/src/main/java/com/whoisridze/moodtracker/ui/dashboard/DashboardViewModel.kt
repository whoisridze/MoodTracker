package com.whoisridze.moodtracker.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whoisridze.moodtracker.domain.model.MoodEntry
import com.whoisridze.moodtracker.domain.model.MoodValue
import com.whoisridze.moodtracker.domain.repository.MoodRepository
import com.whoisridze.moodtracker.domain.usecase.CalculateStreaksUseCase
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardViewModel(
    private val repository: MoodRepository,
    private val calculateStreaksUseCase: CalculateStreaksUseCase = CalculateStreaksUseCase()
) : ViewModel() {

    private val _moodDates = MutableLiveData<Set<LocalDate>>()
    val moodDates: LiveData<Set<LocalDate>> = _moodDates

    private val _morningPercentage = MutableLiveData<Int>()
    val morningPercentage: LiveData<Int> = _morningPercentage

    private val _afternoonPercentage = MutableLiveData<Int>()
    val afternoonPercentage: LiveData<Int> = _afternoonPercentage

    private val _eveningPercentage = MutableLiveData<Int>()
    val eveningPercentage: LiveData<Int> = _eveningPercentage

    private val _recentTrendMessage = MutableLiveData<String>()
    val recentTrendMessage: LiveData<String> = _recentTrendMessage

    private val _currentStreak = MutableLiveData<Int>()
    val currentStreak: LiveData<Int> = _currentStreak

    private val _bestStreak = MutableLiveData<Int>()
    val bestStreak: LiveData<Int> = _bestStreak

    private val _motivationalMessage = MutableLiveData<String>()
    val motivationalMessage: LiveData<String> = _motivationalMessage

    init {
        loadMoodData()
    }

    private fun loadMoodData() {
        viewModelScope.launch {
            val entries = repository.getMoods()
            val dates = entries.map { it.date }.toSet()
            _moodDates.postValue(dates)

            calculateTimeOfDayPercentages(entries)
            analyzeRecentMoodTrend(entries)

            val streakResult = calculateStreaksUseCase.execute(entries)
            _currentStreak.postValue(streakResult.currentStreak)
            _bestStreak.postValue(streakResult.bestStreak)
            _motivationalMessage.postValue(generateMotivationalMessage(streakResult.currentStreak))
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

    private fun analyzeRecentMoodTrend(entries: List<MoodEntry>) {
        if (entries.size < 3) {
            _recentTrendMessage.value = "Log your mood daily to see trends"
            return
        }

        val today = LocalDate.now()
        val recentEntries = entries.filter {
            it.date.isAfter(today.minusDays(30))
        }.sortedBy { it.date }

        if (recentEntries.isEmpty()) {
            _recentTrendMessage.value = "Start tracking your mood to see trends"
            return
        }

        val last3 = recentEntries.takeLast(3)
        val last3Count = last3.groupingBy { it.moodType }.eachCount()
        val dominantLast3 = last3Count.maxByOrNull { it.value }

        if (dominantLast3 != null && dominantLast3.value == 3) {
            _recentTrendMessage.value = when (dominantLast3.key) {
                MoodValue.GREAT -> "You've been feeling great lately"
                MoodValue.GOOD -> "You've been in a good mood recently"
                MoodValue.NEUTRAL -> "Your mood has been steady lately"
                MoodValue.BAD -> "You've been feeling down recently"
                MoodValue.AWFUL -> "You've been having a difficult time lately"
                else -> "Your mood has been mixed this month"
            }
            return
        }

        val last5 = recentEntries.takeLast(5)
        if (last5.size >= 4) {
            val last5Count = last5.groupingBy { it.moodType }.eachCount()
            val dominantLast5 = last5Count.maxByOrNull { it.value }

            if (dominantLast5 != null && dominantLast5.value >= 4) {
                _recentTrendMessage.value = when (dominantLast5.key) {
                    MoodValue.GREAT -> "You've been feeling great lately"
                    MoodValue.GOOD -> "You've been in a good mood recently"
                    MoodValue.NEUTRAL -> "Your mood has been steady lately"
                    MoodValue.BAD -> "You've been feeling down recently"
                    MoodValue.AWFUL -> "You've been having a difficult time lately"
                    else -> "Your mood has been mixed this month"
                }
                return
            }
        }

        val shortTerm = recentEntries.takeLast(minOf(7, recentEntries.size))
        val shortTermSlope = calculateTrendSlope(shortTerm)

        val slope = calculateTrendSlope(recentEntries)

        val effectiveSlope =
            if (Math.abs(shortTermSlope) > Math.abs(slope)) shortTermSlope else slope

        _recentTrendMessage.value = when {
            effectiveSlope > 0.3 -> "Your mood has been improving significantly recently"
            effectiveSlope > 0.1 -> "Your mood shows a positive trend lately"
            effectiveSlope < -0.3 -> "Your mood has been declining recently"
            effectiveSlope < -0.1 -> "Your mood shows a slight downward trend"
            else -> "Your mood has been mixed this month"
        }
    }

    private fun calculateTrendSlope(entries: List<MoodEntry>): Double {
        if (entries.size < 3) return 0.0

        val x = (0 until entries.size).map { it.toDouble() }
        val y = entries.map { moodToNumeric(it.moodType) }

        val n = entries.size
        val sumX = x.sum()
        val sumY = y.sum()
        val sumXY = x.zip(y).sumOf { it.first * it.second }
        val sumX2 = x.sumOf { it * it }

        return (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)
    }

    private fun moodToNumeric(mood: MoodValue): Double {
        return when (mood) {
            MoodValue.AWFUL -> 1.0
            MoodValue.BAD -> 2.0
            MoodValue.NEUTRAL -> 3.0
            MoodValue.GOOD -> 4.0
            MoodValue.GREAT -> 5.0
        }
    }

    private fun generateMotivationalMessage(streak: Int): String {
        return when {
            streak == 0 -> "Start your streak today!"
            streak == 1 -> "Great start! Keep going!"
            streak in 2..3 -> "You're building momentum!"
            streak in 4..6 -> "Impressive consistency!"
            streak == 7 -> "A full week - amazing work!"
            streak in 8..13 -> "You're making this a habit!"
            streak in 14..20 -> "Two weeks strong! Outstanding!"
            streak in 21..29 -> "You're truly dedicated!"
            streak == 30 -> "A whole month! Incredible!"
            streak > 30 -> "You're unstoppable! ${streak} days!"
            else -> "Keep tracking your moods!"
        }
    }

    fun refreshData() {
        loadMoodData()
    }
}