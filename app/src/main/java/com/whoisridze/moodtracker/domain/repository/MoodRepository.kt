package com.whoisridze.moodtracker.domain.repository

import com.whoisridze.moodtracker.domain.model.MoodEntry

interface MoodRepository {
    suspend fun saveMood(moodEntry: MoodEntry)
    suspend fun getMoods(): List<MoodEntry>
    suspend fun getCurrentStreak(): Int
    suspend fun getBestStreak(): Int
}