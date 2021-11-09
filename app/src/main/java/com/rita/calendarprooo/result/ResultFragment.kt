package com.rita.calendarprooo.result

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.rita.calendarprooo.R
import com.rita.calendarprooo.databinding.FragmentResultBinding
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar


class ResultFragment:Fragment() {
    private val viewModel : ResultViewModel by lazy {
        ViewModelProvider(this).get(ResultViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //layout binding
        val binding: FragmentResultBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_result, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        // pie chart setup
        val piechart = binding.barPie

        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(100F,"apple"))
        entries.add(PieEntry(200F,"banana"))

        val colors = mutableListOf<Int>()
        colors.add(resources.getColor(R.color.pink_F2E5D9))
        colors.add(resources.getColor(R.color.red_CF6E62))
        colors.add(resources.getColor(R.color.purple_200))

        val dataSet = PieDataSet(entries,"label")
        dataSet.setColors(colors)

        val pieData = PieData(dataSet)
        pieData.setDrawValues(true)
        pieData.setValueTextSize(15f)
        pieData.setValueTextColor(Color.WHITE)

        piechart.setData(pieData)
        piechart.invalidate()
        piechart.setEntryLabelColor(Color.WHITE)
        piechart.setEntryLabelTextSize(15f)


        //calendar
        val collapsibleCalendar: CollapsibleCalendar = binding.calendarView
        collapsibleCalendar.setCalendarListener(object : CollapsibleCalendar.CalendarListener {
            override fun onDayChanged() {}
            override fun onClickListener() {}
            override fun onDaySelect() {
                val day = collapsibleCalendar.selectedDay
                val dateSelected = ""+day!!.day + "-" + (day.month + 1) + "-" + day.year

                //viewModel.convertToTimeStamp(dateSelected)

                Log.i("Rita", "Selected Day: $dateSelected")
            }

            override fun onItemClick(view: View) {}
            override fun onDataUpdate() {}
            override fun onMonthChange() {}
            override fun onWeekChange(i: Int) {}
        })

        return binding.root

    }

    }