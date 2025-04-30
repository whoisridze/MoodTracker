package com.whoisridze.moodtracker.data.model

import com.whoisridze.moodtracker.domain.model.MoodValue
import java.time.LocalDate
import java.time.LocalTime

data class MoodEntry(
    val date: LocalDate,
    val time: LocalTime,
    val moodType: MoodValue,
    val reason: String? = null
)