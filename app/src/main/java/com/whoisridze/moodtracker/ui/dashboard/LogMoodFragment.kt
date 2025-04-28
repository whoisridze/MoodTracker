package com.whoisridze.moodtracker.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.whoisridze.moodtracker.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LogMoodFragment : Fragment(R.layout.fragment_log_mood) {

    private enum class Mood(
        @IdRes val btnId: Int,
        @DrawableRes val iconRes: Int,
        @DrawableRes val bgRes: Int,
        @StringRes val labelRes: Int
    ) {
        AWFUL(
            R.id.btnMoodAwful,
            R.drawable.ic_mood_awful,
            R.drawable.bg_mood_awful,
            R.string.tvMoodAwfulText
        ),
        BAD(
            R.id.btnMoodBad,
            R.drawable.ic_mood_bad,
            R.drawable.bg_mood_bad,
            R.string.tvMoodBadText
        ),
        NEUTRAL(
            R.id.btnMoodNeutral,
            R.drawable.ic_mood_neutral,
            R.drawable.bg_mood_neutral,
            R.string.tvMoodNeutralText
        ),
        GOOD(
            R.id.btnMoodGood,
            R.drawable.ic_mood_good,
            R.drawable.bg_mood_good,
            R.string.tvMoodGoodText
        ),
        GREAT(
            R.id.btnMoodGreat,
            R.drawable.ic_mood_great,
            R.drawable.bg_mood_great,
            R.string.tvMoodGreatText
        );
    }

    private lateinit var motion: MotionLayout
    private lateinit var ivCenter: ImageView
    private lateinit var etReason: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var tvHeader: TextView
    private lateinit var tvLabel: TextView

    private var selectedMood: Mood? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motion = view.findViewById(R.id.motionRoot)
        ivCenter = view.findViewById(R.id.ivSelectedMood)
        etReason = view.findViewById(R.id.etReason)
        btnSend = view.findViewById(R.id.btnSend)
        tvHeader = view.findViewById(R.id.tvHeaderSelected)
        tvLabel = view.findViewById(R.id.tvSelectedMoodLabel)

        fillDateTime(view)
        setupButtons(view)
        motion.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionCompleted(layout: MotionLayout, currentId: Int) {
                val btnClose = view?.findViewById<ImageButton>(R.id.btnClose)
                if (currentId == R.id.start) {
                    setLabelsVisible(true)

                    ivCenter.isVisible = false
                    etReason.isVisible = false
                    btnSend.isVisible = false
                    tvHeader.isVisible = false
                    tvLabel.isVisible = false

                    Mood.entries.forEach { entry ->
                        view?.findViewById<ImageButton>(entry.btnId)?.apply {
                            alpha = 1f
                            isEnabled = true
                        }
                    }

                    motion.setBackgroundResource(R.drawable.bg_gradient)
                    btnClose?.setImageResource(R.drawable.ic_close)
                    btnClose?.contentDescription = getString(R.string.closeButtonDesc)

                    selectedMood = null
                } else if (currentId == R.id.end) {
                    btnClose?.setImageResource(R.drawable.ic_arrow_back)
                }
            }

            override fun onTransitionChange(
                l: MotionLayout,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionStarted(l: MotionLayout, startId: Int, endId: Int) {}
            override fun onTransitionTrigger(
                l: MotionLayout,
                triggerId: Int,
                pos: Boolean,
                v: Float
            ) {
            }
        })
    }

    private fun fillDateTime(root: View) {
        root.findViewById<android.widget.TextView>(R.id.tvDate)
            .text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM, yyyy"))
        root.findViewById<android.widget.TextView>(R.id.tvTime)
            .text = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    private fun setupButtons(root: View) {
        Mood.entries.forEach { mood ->
            root.findViewById<ImageButton>(mood.btnId).setOnClickListener {
                onMoodChosen(mood)
            }
        }

        root.findViewById<ImageButton>(R.id.btnClose).setOnClickListener {
            if (motion.currentState == R.id.end) {
                motion.transitionToStart()
            } else {
                findNavController().navigateUp()
            }
        }

        btnSend.setOnClickListener {
            selectedMood?.let { m ->
                // TODO: repository.save(m, etReason.text.toString()) in JSON
            }
            findNavController().navigateUp()
        }
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
