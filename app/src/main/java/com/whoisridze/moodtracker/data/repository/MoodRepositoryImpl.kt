package com.whoisridze.moodtracker.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.whoisridze.moodtracker.data.local.gson.LocalDateAdapter
import com.whoisridze.moodtracker.data.local.gson.LocalTimeAdapter
import com.whoisridze.moodtracker.domain.model.MoodEntry
import com.whoisridze.moodtracker.domain.repository.MoodRepository
import com.whoisridze.moodtracker.domain.usecase.CalculateStreaksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime

class MoodRepositoryImpl(private val context: Context) : MoodRepository {
    private val fileName = "mood_entries.json"
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
        .create()

    override suspend fun saveMood(moodEntry: MoodEntry) {
        withContext(Dispatchers.IO) {
            val moods = getMoods().toMutableList()
            moods.removeAll { it.date == moodEntry.date }
            moods.add(moodEntry)
            moods.sortBy { it.date }
            val json = gson.toJson(moods)
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
        }
    }

    override suspend fun getMoods(): List<MoodEntry> {
        return withContext(Dispatchers.IO) {
            try {
                val file = context.getFileStreamPath(fileName)
                if (file != null && file.exists()) {
                    context.openFileInput(fileName).bufferedReader().use {
                        val type = object : TypeToken<List<MoodEntry>>() {}.type
                        gson.fromJson<List<MoodEntry>>(it, type)
                    }
                } else {
                    emptyList()
                }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    private val streaksUseCase = CalculateStreaksUseCase()

    override suspend fun getCurrentStreak(): Int {
        return streaksUseCase.execute(getMoods()).currentStreak
    }

    override suspend fun getBestStreak(): Int {
        return streaksUseCase.execute(getMoods()).bestStreak
    }
}