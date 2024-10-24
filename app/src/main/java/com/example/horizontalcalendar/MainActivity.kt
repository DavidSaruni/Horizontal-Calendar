package com.example.horizontalcalendar

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.horizontalcalendar.databinding.ActivityMainBinding
import com.example.horizontalcalendar.databinding.DateItemBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), CalendarAdapter.CalendarInterface {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private var mStartD: Date? = null

    private val calendarAdapter = CalendarAdapter(this, arrayListOf())
    private val calendarList = ArrayList<CalendarData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize binding before setting the content view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
        initCalendar()
    }

    private fun init() {
        binding.apply {
            monthYearPicker.text = sdf.format(cal.time)
            calendarView.setHasFixedSize(true)
            calendarView.adapter = calendarAdapter
            monthYearPicker.setOnClickListener {
                displayDatePicker()
            }
        }
    }

    private fun initCalendar() {
        mStartD = Date()
        cal.time = Date()
        getDates()
    }

    private fun displayDatePicker() {
        val materialDateBuilder: MaterialDatePicker.Builder<Long> =
            MaterialDatePicker.Builder.datePicker()
        materialDateBuilder.setTitleText("Select Date")

        val materialDatePicker = materialDateBuilder.build()
        materialDatePicker.show(supportFragmentManager, "DATE_PICKER")
        materialDatePicker.addOnPositiveButtonClickListener {
            try {
                mStartD = Date(it)
                binding.monthYearPicker.text = sdf.format(it)
                cal.time = Date(it)

                getDates()
            } catch (e: ParseException) {
                Log.e(TAG, "displayDatePicker: ${e.message}")
            }
        }
    }

    private fun getDates() {
        val dateList = ArrayList<CalendarData>()
        val dates = ArrayList<Date>()
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        while (dates.size < maxDaysInMonth) {
            dates.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Populate the dateList with the CalendarData objects
        dates.forEach { date ->
            dateList.add(CalendarData(date, isSelected = date == mStartD))
        }

        calendarList.clear()
        calendarList.addAll(dateList)
        calendarAdapter.updateList(dateList)

        // Scroll to the selected date position
        for (item in dateList.indices) {
            if (dateList[item].data == mStartD) {
                calendarAdapter.setPosition(item)
                binding.calendarView.scrollToPosition(item)
            }
        }
    }

    override fun onDateSelected(calendarData: CalendarData, position: Int, day: TextView) {
        calendarList.forEachIndexed { index, calendarData ->
            calendarData.isSelected = index == position
        }
    }
}
