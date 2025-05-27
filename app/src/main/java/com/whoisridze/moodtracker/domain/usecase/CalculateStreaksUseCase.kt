package com.whoisridze.moodtracker.domain.usecase

import com.whoisridze.moodtracker.domain.model.MoodEntry
import java.time.LocalDate

class CalculateStreaksUseCase {

    data class StreakResult(val currentStreak: Int, val bestStreak: Int)

    fun execute(entries: List<MoodEntry>): StreakResult {
        if (entries.isEmpty()) {
            return StreakResult(0, 0)
        }

        val entriesByDate = entries.groupBy { it.date }
        val dates = entriesByDate.keys.sorted()

        val today = LocalDate.now()
        var currentStreak = 0

        if (entriesByDate.containsKey(today)) {
            currentStreak = 1
            var previousDate = today
            var checkDate = today.minusDays(1)

            while (entriesByDate.containsKey(checkDate) && checkDate.plusDays(1)
                    .isEqual(previousDate)
            ) {
                currentStreak++
                previousDate = checkDate
                checkDate = checkDate.minusDays(1)
            }
        } else {
            val yesterday = today.minusDays(1)
            if (entriesByDate.containsKey(yesterday)) {
                currentStreak = 1
                var previousDate = yesterday
                var checkDate = yesterday.minusDays(1)

                while (entriesByDate.containsKey(checkDate) && checkDate.plusDays(1)
                        .isEqual(previousDate)
                ) {
                    currentStreak++
                    previousDate = checkDate
                    checkDate = checkDate.minusDays(1)
                }
            }
        }

        var bestStreak = 0
        var tempStreak = 0

        for (i in dates.indices) {
            if (i == 0) {
                tempStreak = 1
            } else {
                if (dates[i].minusDays(1).isEqual(dates[i - 1])) {
                    tempStreak++
                } else {
                    tempStreak = 1
                }
            }
            bestStreak = maxOf(bestStreak, tempStreak)
        }

        return StreakResult(currentStreak, bestStreak)
    }
}