package com.whoisridze.moodtracker.data.model

import java.time.LocalDate
import java.time.LocalTime

data class MoodEntry(
    val date: LocalDate,
    val time: LocalTime,
    val moodType: String,
    val reason: String? = null
)