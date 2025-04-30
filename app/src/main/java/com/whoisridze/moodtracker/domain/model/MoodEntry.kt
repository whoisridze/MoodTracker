package com.whoisridze.moodtracker.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class MoodEntry(
    val date: LocalDate,
    val time: LocalTime,
    val moodType: MoodValue,
    val reason: String? = null
)