package com.whoisridze.moodtracker.ui.stats

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.whoisridze.moodtracker.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MoodMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    private val tvDate: TextView = findViewById(R.id.tvMarkerDate)
    private val tvMood: TextView = findViewById(R.id.tvMarkerMood)
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

    override fun refreshContent(entry: Entry, highlight: Highlight) {
        val lineChart = chartView as? LineChart
        val index = entry.x.toInt()

        val date =
            if (lineChart?.data?.dataSets?.get(0)?.getEntryForIndex(index)?.data is LocalDate) {
                (lineChart.data.dataSets[0].getEntryForIndex(index).data as LocalDate).format(
                    dateFormatter
                )
            } else {
                val dates = lineChart?.xAxis?.valueFormatter as? IndexAxisValueFormatter
                dates?.getFormattedValue(entry.x) ?: ""
            }

        val moodText = when (entry.y.toInt()) {
            1 -> "Awful"
            2 -> "Bad"
            3 -> "Neutral"
            4 -> "Good"
            5 -> "Great"
            else -> "Unknown"
        }

        tvDate.text = date
        tvMood.text = moodText
        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height - 10f)
    }
}