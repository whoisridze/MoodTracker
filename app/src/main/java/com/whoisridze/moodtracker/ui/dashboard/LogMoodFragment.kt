package com.whoisridze.moodtracker.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.whoisridze.moodtracker.R
import com.whoisridze.moodtracker.data.repository.MoodRepositoryImpl
import com.whoisridze.moodtracker.domain.model.MoodValue
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LogMoodFragment : Fragment(R.layout.fragment_log_mood) {

    private enum class Mood(
        @DrawableRes val iconRes: Int,
        @StringRes val labelRes: Int,
        @DrawableRes val bgRes: Int,
        @IdRes val btnId: Int
    ) {
        AWFUL(R.drawable.ic_mood_awful, R.string.tvMoodAwfulText, R.drawable.bg_mood_awful, R.id.btnMoodAwful),
        BAD(R.drawable.ic_mood_bad, R.string.tvMoodBadText, R.drawable.bg_mood_bad, R.id.btnMoodBad),
        NEUTRAL(R.drawable.ic_mood_neutral, R.string.tvMoodNeutralText, R.drawable.bg_mood_neutral, R.id.btnMoodNeutral),
        GOOD(R.drawable.ic_mood_good, R.string.tvMoodGoodText, R.drawable.bg_mood_good, R.id.btnMoodGood),
        GREAT(R.drawable.ic_mood_great, R.string.tvMoodGreatText, R.drawable.bg_mood_great, R.id.btnMoodGreat)
    }

    private lateinit var motion: MotionLayout
    private lateinit var ivCenter: ImageView
    private lateinit var etReason: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnClose: ImageButton
    private lateinit var tvHeader: TextView
    private lateinit var tvLabel: TextView
    private lateinit var tvTime: TextView
    private lateinit var viewModel: LogMoodViewModel

    private var selectedMood: Mood? = null
    private lateinit var selectedDate: LocalDate
    private lateinit var selectedTime: LocalTime
    private var isMoodSelected = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motion = view.findViewById(R.id.motionRoot)
        ivCenter = view.findViewById(R.id.ivSelectedMood)
        etReason = view.findViewById(R.id.etReason)
        btnSend = view.findViewById(R.id.btnSend)
        btnClose = view.findViewById(R.id.btnClose)
        tvHeader = view.findViewById(R.id.tvHeaderSelected)
        tvLabel = view.findViewById(R.id.tvSelectedMoodLabel)
        tvTime = view.findViewById(R.id.tvTime)

        val repository = MoodRepositoryImpl(requireContext())
        val factory = LogMoodViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[LogMoodViewModel::class.java]

        selectedDate = getSelectedDateFromArguments()
        selectedTime = LocalTime.now().withSecond(0).withNano(0)

        fillDateTime(view, selectedDate)
        setupTimePickerClick(view)
        setupButtons(view)

        motion.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionCompleted(layout: MotionLayout, currentId: Int) {
                if (currentId == R.id.end) {
                    setLabelsVisible(false)
                }
            }

            override fun onTransitionChange(
                layout: MotionLayout,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionStarted(l: MotionLayout, startId: Int, endId: Int) {}
            override fun onTransitionTrigger(
                layout: MotionLayout,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }
        })
    }

    private fun setupTimePickerClick(view: View) {
        view.findViewById<View>(R.id.timePickerContainer).setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun showTimePickerDialog() {
        val is24HourFormat = android.text.format.DateFormat.is24HourFormat(requireContext())

        val timePickerDialog = MaterialTimePicker.Builder()
            .setTimeFormat(if (is24HourFormat) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H)
            .setHour(selectedTime.hour)
            .setMinute(selectedTime.minute)
            .setTitleText(getString(R.string.tvTimeValue))
            .build()

        timePickerDialog.addOnPositiveButtonClickListener {
            selectedTime = LocalTime.of(timePickerDialog.hour, timePickerDialog.minute)
            updateTimeDisplay()
        }

        timePickerDialog.show(childFragmentManager, "TIME_PICKER")
    }

    private fun updateTimeDisplay() {
        tvTime.text = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    private fun getSelectedDateFromArguments(): LocalDate {
        val year = arguments?.getInt("selectedYear", -1) ?: -1
        val month = arguments?.getInt("selectedMonth", -1) ?: -1
        val day = arguments?.getInt("selectedDay", -1) ?: -1

        return if (year > 0 && month >= 0 && day > 0) {
            LocalDate.of(year, month + 1, day)
        } else {
            LocalDate.now()
        }
    }

    private fun fillDateTime(root: View, date: LocalDate) {
        root.findViewById<TextView>(R.id.tvDate)
            .text = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        updateTimeDisplay()
    }

    private fun setupButtons(root: View) {
        Mood.entries.forEach { mood ->
            root.findViewById<ImageButton>(mood.btnId)?.setOnClickListener {
                onMoodChosen(mood)
            }
        }

        btnClose.setOnClickListener {
            if (isMoodSelected) {
                resetToMoodSelection()
            } else {
                findNavController().navigateUp()
            }
        }

        btnSend.setOnClickListener {
            selectedMood?.let { m ->
                val moodValue = MoodValue.valueOf(m.name)
                val reason = etReason.text.toString().takeIf { it.isNotBlank() }

                viewModel.saveMood(selectedDate, selectedTime, moodValue, reason)

                Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
            }
            findNavController().navigateUp()
        }
    }

    private fun resetToMoodSelection() {
        isMoodSelected = false
        selectedMood = null

        motion.setBackgroundResource(R.drawable.bg_gradient)

        btnClose.setImageResource(R.drawable.ic_close)

        motion.transitionToStart()

        Mood.entries.forEach { entry ->
            view?.findViewById<ImageButton>(entry.btnId)?.isEnabled = true
        }

        ivCenter.isVisible = false
        etReason.isVisible = false
        btnSend.isVisible = false
        tvHeader.isVisible = false
        tvLabel.isVisible = false

        etReason.setText("")

        setLabelsVisible(true)
    }

    private val moodLabels = listOf(
        R.id.tvMoodAwful, R.id.tvMoodBad, R.id.tvMoodNeutral,
        R.id.tvMoodGood, R.id.tvMoodGreat
    )

    private fun setLabelsVisible(visible: Boolean) {
        moodLabels.forEach { id ->
            view?.findViewById<View>(id)?.alpha = if (visible) 1f else 0f
        }
    }

    private fun onMoodChosen(mood: Mood) {
        selectedMood = mood
        isMoodSelected = true

        btnClose.setImageResource(R.drawable.ic_arrow_back)

        ivCenter.setImageResource(mood.iconRes)
        tvLabel.text = getString(mood.labelRes)
        motion.setBackgroundResource(mood.bgRes)

        ivCenter.isVisible = true
        etReason.isVisible = true
        btnSend.isVisible = true
        tvHeader.isVisible = true
        tvLabel.isVisible = true

        Mood.entries.forEach { entry ->
            view?.findViewById<ImageButton>(entry.btnId)?.isEnabled = false
        }

        motion.transitionToEnd()
    }
}