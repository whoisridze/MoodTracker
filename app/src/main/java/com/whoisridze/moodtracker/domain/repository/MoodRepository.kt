package com.whoisridze.moodtracker.domain.repository

import com.whoisridze.moodtracker.data.model.MoodEntry

interface MoodRepository {
    suspend fun saveMood(moodEntry: MoodEntry)
    suspend fun getMoods(): List<MoodEntry>
}