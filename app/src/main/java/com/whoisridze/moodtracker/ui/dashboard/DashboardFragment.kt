package com.whoisridze.moodtracker.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.whoisridze.moodtracker.R
import java.time.LocalDate
import java.time.YearMonth
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.whoisridze.moodtracker.data.repository.MoodRepositoryImpl
import java.time.format.TextStyle
import java.util.Locale

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var calendarView: CalendarView
    private var selectedDate: LocalDate? = null
    private val moodEntries = mutableSetOf<LocalDate>()
    private lateinit var viewModel: DashboardViewModel

    private lateinit var tvMorningPercent: TextView
    private lateinit var tvAfternoonPercent: TextView
    private lateinit var tvEveningPercent: TextView

    private lateinit var tvRecentTrendMessage: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)

        tvMorningPercent = view.findViewById(R.id.tvMorningPercent)
        tvAfternoonPercent = view.findViewById(R.id.tvAfternoonPercent)
        tvEveningPercent = view.findViewById(R.id.tvEveningPercent)

        tvRecentTrendMessage = view.findViewById(R.id.tvRecentTrendMessage)

        val factory = DashboardViewModelFactory(MoodRepositoryImpl(requireContext()))
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]

        viewModel.moodDates.observe(viewLifecycleOwner) { dates ->
            moodEntries.clear()
            moodEntries.addAll(dates)
            calendarView.notifyCalendarChanged()
        }

        viewModel.recentTrendMessage.observe(viewLifecycleOwner) { message ->
            tvRecentTrendMessage.text = message
        }

        viewModel.morningPercentage.observe(viewLifecycleOwner) { percentage ->
            tvMorningPercent.text = getString(R.string.percentageFormat, percentage)
        }

        viewModel.afternoonPercentage.observe(viewLifecycleOwner) { percentage ->
            tvAfternoonPercent.text = getString(R.string.percentageFormat, percentage)
        }

        viewModel.eveningPercentage.observe(viewLifecycleOwner) { percentage ->
            tvEveningPercent.text = getString(R.string.percentageFormat, percentage)
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(6)
        val lastMonth = currentMonth.plusMonths(6)
        val firstDayOfWeek = firstDayOfWeekFromLocale()

        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView: TextView = view.findViewById(R.id.calendarDayText)
            val moodIndicator: View = view.findViewById(R.id.moodIndicator)
            lateinit var day: CalendarDay

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        val today = LocalDate.now()
                        if (!day.date.isAfter(today) && selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date

                            oldDate?.let {
                                calendarView.notifyDateChanged(it)
                            }
                            calendarView.notifyDateChanged(day.date)

                            val bundle = Bundle().apply {
                                putInt("selectedYear", day.date.year)
                                putInt("selectedMonth", day.date.monthValue - 1)
                                putInt("selectedDay", day.date.dayOfMonth)
                            }
                            findNavController().navigate(
                                R.id.action_dashboardFragment_to_logMoodFragment,
                                bundle
                            )
                        }
                    }
                }
            }
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            private val today = LocalDate.now()

            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.textView
                textView.text = data.date.dayOfMonth.toString()

                container.view.isEnabled = !data.date.isAfter(today)

                container.moodIndicator.visibility = if (moodEntries.contains(data.date))
                    View.VISIBLE else View.INVISIBLE

                if (data.position == DayPosition.MonthDate) {
                    textView.visibility = View.VISIBLE

                    when {
                        today == data.date -> {
                            textView.setBackgroundResource(R.drawable.bg_calendar_selected)
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                        }

                        data.date.isAfter(today) -> {
                            textView.background = null
                            textView.alpha = 1f
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.gray
                                )
                            )
                        }

                        else -> {
                            textView.background = null
                            textView.alpha = 1.0f
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                        }
                    }
                } else {
                    textView.visibility = View.INVISIBLE
                    container.moodIndicator.visibility = View.INVISIBLE
                }
            }
        }

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                val monthName =
                    month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .uppercase()
                val year = month.yearMonth.year
                container.textView.text = getString(R.string.monthYearFormat, monthName, year)
            }
        }

        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)
    }

    class MonthViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.headerMonthText)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }
}