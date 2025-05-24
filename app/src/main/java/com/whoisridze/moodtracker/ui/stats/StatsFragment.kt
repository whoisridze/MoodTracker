package com.whoisridze.moodtracker.ui.stats

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.button.MaterialButton
import com.whoisridze.moodtracker.R
import com.whoisridze.moodtracker.data.repository.MoodRepositoryImpl
import com.whoisridze.moodtracker.domain.model.MoodValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class StatsFragment : Fragment(R.layout.fragment_stats) {

    private lateinit var viewModel: StatsViewModel
    private lateinit var barChart: BarChart
    private lateinit var lineChart: LineChart

    private lateinit var btnPeriodWeek: MaterialButton
    private lateinit var btnPeriodMonth: MaterialButton
    private lateinit var btnPeriodYear: MaterialButton
    private lateinit var btnPeriodAllTime: MaterialButton

    private lateinit var tvMostFrequentMoodValue: TextView
    private lateinit var tvAverageMoodValue: TextView
    private lateinit var tvCurrentStreakValue: TextView
    private lateinit var tvBestStreakValue: TextView
    private lateinit var tvTotalEntriesValue: TextView

    private val periodButtons = mutableListOf<MaterialButton>()
    private val activeButtonTint = 0x7798DFF4.toInt()
    private val inactiveButtonTint = 0x33FFFFFF.toInt()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = MoodRepositoryImpl(requireContext())
        val factory = StatsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[StatsViewModel::class.java]

        initViews(view)
        setupCharts(view)
        setupButtonListeners()
        observeViewModel()

    }

    private fun initViews(view: View) {
        btnPeriodWeek = view.findViewById(R.id.btnPeriodWeek)
        btnPeriodMonth = view.findViewById(R.id.btnPeriodMonth)
        btnPeriodYear = view.findViewById(R.id.btnPeriodYear)
        btnPeriodAllTime = view.findViewById(R.id.btnPeriodAllTime)

        periodButtons.addAll(
            listOf(
                btnPeriodWeek,
                btnPeriodMonth,
                btnPeriodYear,
                btnPeriodAllTime
            )
        )

        tvMostFrequentMoodValue = view.findViewById(R.id.tvMostFrequentMoodValue)
        tvAverageMoodValue = view.findViewById(R.id.tvAverageMoodValue)
        tvCurrentStreakValue = view.findViewById(R.id.tvCurrentStreakValue)
        tvBestStreakValue = view.findViewById(R.id.tvBestStreakValue)
        tvTotalEntriesValue = view.findViewById(R.id.tvTotalEntriesValue)
    }

    private fun setupCharts(view: View) {
        val distributionContainer = view.findViewById<FrameLayout>(R.id.chartMoodDistribution)
        barChart = BarChart(requireContext())
        distributionContainer.addView(barChart)
        setupBarChart()

        val trendContainer = view.findViewById<FrameLayout>(R.id.chartMoodTrend)
        lineChart = LineChart(requireContext())
        trendContainer.addView(lineChart)
        setupLineChart()
    }

    private fun setupBarChart() {
        val nunitoTypeface = ResourcesCompat.getFont(requireContext(), R.font.nunito)

        barChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            isDoubleTapToZoomEnabled = false
            setPinchZoom(false)
            setScaleEnabled(false)

            setNoDataText("Record your mood to see statistics")
            setNoDataTextColor(Color.WHITE)
            setNoDataTextTypeface(nunitoTypeface)

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.typeface = nunitoTypeface
            xAxis.textColor = Color.WHITE
            xAxis.textSize = 12f

            axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(true)
                axisLineWidth = 1f
                axisMinimum = 0f
                typeface = nunitoTypeface
                textColor = Color.WHITE
                textSize = 12f
            }

            axisRight.isEnabled = false

            setExtraOffsets(10f, 10f, 10f, 15f)
        }
    }

    private fun setupLineChart() {
        val nunitoTypeface = ResourcesCompat.getFont(requireContext(), R.font.nunito)

        lineChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)

            isDoubleTapToZoomEnabled = true
            setPinchZoom(true)
            setScaleEnabled(true)
            isDragEnabled = true
            setVisibleXRangeMaximum(10f)

            setNoDataText("Track your mood daily to see patterns")
            setNoDataTextColor(Color.WHITE)
            setNoDataTextTypeface(nunitoTypeface)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                typeface = nunitoTypeface
                textColor = Color.WHITE
                textSize = 11f
                setDrawLabels(false)
                labelRotationAngle = 30f
                labelCount = 5
                setAvoidFirstLastClipping(true)
                spaceMin = 0.2f
                spaceMax = 0.2f
                axisLineWidth = 1f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.WHITE.apply { alpha = 1f }
                gridLineWidth = 0.5f
                typeface = nunitoTypeface
                textColor = Color.WHITE
                textSize = 12f
                axisMinimum = 0.2f
                axisMaximum = 5.5f
                granularity = 1.0f

                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return if (value.toInt() in 1..5) {
                            value.toInt().toString()
                        } else {
                            ""
                        }
                    }
                }
            }

            axisRight.isEnabled = false

            setExtraOffsets(15f, 15f, 15f, 15f)

            animateX(1000)

            val markerView = MoodMarkerView(requireContext(), R.layout.marker_mood_view)
            markerView.chartView = this
            marker = markerView
        }
    }

    private fun setupButtonListeners() {
        btnPeriodWeek.setOnClickListener {
            updateButtonState(it as MaterialButton)
            viewModel.setPeriod(StatsViewModel.StatsPeriod.WEEK)
        }

        btnPeriodMonth.setOnClickListener {
            updateButtonState(it as MaterialButton)
            viewModel.setPeriod(StatsViewModel.StatsPeriod.MONTH)
        }

        btnPeriodYear.setOnClickListener {
            updateButtonState(it as MaterialButton)
            viewModel.setPeriod(StatsViewModel.StatsPeriod.YEAR)
        }

        btnPeriodAllTime.setOnClickListener {
            updateButtonState(it as MaterialButton)
            viewModel.setPeriod(StatsViewModel.StatsPeriod.ALL_TIME)
        }
    }

    private fun updateButtonState(selectedButton: MaterialButton) {
        periodButtons.forEach { button ->
            button.setBackgroundColor(inactiveButtonTint)
        }

        selectedButton.setBackgroundColor(activeButtonTint)
    }

    private fun observeViewModel() {
        viewModel.selectedPeriod.observe(viewLifecycleOwner) { period ->
            val buttonToHighlight = when (period) {
                StatsViewModel.StatsPeriod.WEEK -> btnPeriodWeek
                StatsViewModel.StatsPeriod.MONTH -> btnPeriodMonth
                StatsViewModel.StatsPeriod.YEAR -> btnPeriodYear
                StatsViewModel.StatsPeriod.ALL_TIME -> btnPeriodAllTime
            }
            updateButtonState(buttonToHighlight)
        }

        viewModel.moodDistribution.observe(viewLifecycleOwner) { distribution ->
            updateDistributionChart(distribution)
        }

        viewModel.moodTimeline.observe(viewLifecycleOwner) { timeline ->
            updateTimelineChart(timeline)
        }

        viewModel.mostFrequentMood.observe(viewLifecycleOwner) { mood ->
            tvMostFrequentMoodValue.text = mood?.name?.lowercase()?.replaceFirstChar {
                it.uppercase()
            } ?: getString(R.string.noData)
        }

        viewModel.averageMood.observe(viewLifecycleOwner) { average ->
            tvAverageMoodValue.text = String.format("%.1f/5", average)
        }

        viewModel.currentStreak.observe(viewLifecycleOwner) { streak ->
            tvCurrentStreakValue.text = resources.getQuantityString(
                R.plurals.daysCount, streak, streak
            )
        }

        viewModel.bestStreak.observe(viewLifecycleOwner) { streak ->
            tvBestStreakValue.text = resources.getQuantityString(
                R.plurals.daysCount, streak, streak
            )
        }

        viewModel.totalEntries.observe(viewLifecycleOwner) { count ->
            tvTotalEntriesValue.text = count.toString()
        }
    }

    private fun updateDistributionChart(distribution: Map<MoodValue, Int>) {
        val hasData = distribution.values.any { it > 0 }
        if (!hasData) {
            barChart.clear()
            barChart.invalidate()
            return
        }

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        val colors = ArrayList<Int>()

        var maxCount = 0

        val moodValues = MoodValue.values()
        moodValues.forEachIndexed { index, moodValue ->
            val count = distribution[moodValue] ?: 0
            maxCount = maxOf(maxCount, count)
            entries.add(BarEntry(index.toFloat(), count.toFloat()))
            labels.add(moodValue.name.lowercase().replaceFirstChar {
                it.uppercase()
            })

            val color = when (moodValue) {
                MoodValue.AWFUL -> Color.rgb(208, 64, 64)
                MoodValue.BAD -> Color.rgb(255, 165, 0)
                MoodValue.NEUTRAL -> Color.rgb(135, 206, 235)
                MoodValue.GOOD -> Color.rgb(154, 205, 50)
                MoodValue.GREAT -> Color.rgb(64, 208, 64)
            }
            colors.add(color)
        }

        val dataSet = BarDataSet(entries, "Mood Distribution")
        dataSet.colors = colors
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.WHITE

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        val typeface = ResourcesCompat.getFont(requireContext(), R.font.nunito)
        dataSet.valueTypeface = typeface

        val barData = BarData(dataSet)
        barData.barWidth = 0.7f

        barChart.axisLeft.apply {
            granularity = 1f
            axisMaximum = if (maxCount > 0) maxCount.toFloat() else 1f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
            spaceTop = 0f
        }

        barChart.axisRight.apply {
            isEnabled = false
            spaceTop = 0f
        }

        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.setFitBars(true)
        barChart.invalidate()
    }

    private fun updateTimelineChart(timeline: Map<LocalDate, MoodValue>) {
        if (timeline.isEmpty()) {
            lineChart.clear()
            lineChart.invalidate()
            return
        }

        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()

        val dateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())

        val sortedDates = timeline.keys.sortedBy { it }

        sortedDates.forEachIndexed { index, date ->
            val moodValue = timeline[date]
            val moodScore = when (moodValue) {
                MoodValue.AWFUL -> 1f
                MoodValue.BAD -> 2f
                MoodValue.NEUTRAL -> 3f
                MoodValue.GOOD -> 4f
                MoodValue.GREAT -> 5f
                null -> 3f
            }

            val entry = Entry(index.toFloat(), moodScore)
            entry.data = date
            entries.add(entry)
            labels.add(date.format(dateFormatter))
        }

        val dataSet = LineDataSet(entries, "Mood Trend")
        dataSet.apply {
            color = Color.WHITE
            lineWidth = 2.5f
            setDrawCircles(true)
            setCircleColor(Color.WHITE)
            circleRadius = 4f
            circleHoleRadius = 2f
            circleHoleColor = ContextCompat.getColor(requireContext(), R.color.paletteColor2)

            setDrawFilled(true)
            fillDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    Color.parseColor("#80FFFFFF"),
                    Color.parseColor("#00FFFFFF")
                )
            )

            setDrawValues(false)
            highLightColor = Color.WHITE
            highlightLineWidth = 1f
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        lineChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            granularity = 1f
            setAvoidFirstLastClipping(true)

            when {
                entries.size == 1 -> {
                    labelCount = 1
                    labelRotationAngle = 0f
                }

                entries.size <= 7 -> {
                    labelCount = entries.size
                    labelRotationAngle = 0f
                }

                else -> {
                    labelCount = minOf(entries.size, 10)
                    labelRotationAngle = 45f
                }
            }
        }

        lineChart.xAxis.axisMinimum = 0f

        if (entries.size == 1) {
            lineChart.xAxis.axisMinimum = -0.5f
            lineChart.xAxis.axisMaximum = 0.5f
            lineChart.setVisibleXRangeMinimum(1f)
        } else {
            lineChart.xAxis.axisMinimum = 0f
            lineChart.xAxis.axisMaximum = (entries.size - 1) + 0f

            if (entries.size <= 7) {
                lineChart.setVisibleXRangeMaximum(7f)
                lineChart.moveViewToX(0f)
            } else {
                lineChart.fitScreen()
            }
        }

        lineChart.moveViewToX(0f)

        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }
}