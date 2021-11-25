package com.rita.calendarprooo.result

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.rita.calendarprooo.R
import com.rita.calendarprooo.databinding.FragmentResultBinding
import com.rita.calendarprooo.ext.getColorCode
import com.rita.calendarprooo.ext.getVmFactory
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar


class ResultFragment : Fragment() {

    private val viewModel by viewModels<ResultViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentResultBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_result, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        viewModel.doneListReset.observe(viewLifecycleOwner, {
            Log.d("Rita", "result doneListReset observe: $it")

            it?.let {
                if (it) {
                    viewModel.doneList.observe(viewLifecycleOwner, {
                        Log.i("Rita", "result doneList observe: $it")
                        it?.let {
                            viewModel.countForCategory(it)
                        }
                    })
                    // assign the reference by binding the viewModel again
                    binding.viewModel = viewModel
                    viewModel.doneListReset.value = null
                }
            }

        })


        // read Plans when date selected changed
        viewModel.selectedEndTime.observe(viewLifecycleOwner, {
            Log.i("Rita", "result selectedEndTime observe: $it")

            it?.let {
                viewModel.readDone()
                viewModel.readPlanFromToday()

                // When the livedata is assigned, it will be assigned for different memory reference.
                // So we need to set Observer here, readListFromToday as livedata can be observed
                // for the same reference
                viewModel.readListFromToday.observe(viewLifecycleOwner, {
                        Log.i("Rita", "result readListFromToday observe: $it")
                        it?.let {
                            viewModel.readPlanBeforeToday()

                            viewModel.readListBeforeToday.observe(viewLifecycleOwner, {
                                    Log.i("Rita", "result readListBeforeToday observe: $it")
                                    it?.let {
                                        viewModel.readPlanInTotal()
                                    }
                            })
                        }
                })
            }
        })


        // pie chart setup
        val pieChart = binding.barPie

        viewModel.categoryForDoneList.observe(viewLifecycleOwner,{
            Log.i("Rita", "result categoryForDoneList observe: $it")

            it?.let {
                val entries = mutableListOf<PieEntry>()
                for (item in it) {
                    entries.add(PieEntry(item.value, item.key))
                }
                binding.viewModel = viewModel
                viewModel.pieEntryList.value = entries
            }
        })


        val colors = mutableListOf<Int>(
            getColorCode(R.color.pink_F2E5D9),
            getColorCode(R.color.red_CF6E62),
            getColorCode(R.color.green_97A97C), getColorCode(R.color.pink_CCB7AE),
            getColorCode(R.color.yellow_F6BD60), getColorCode(R.color.black_424B54),
            getColorCode(R.color.green_84A59D), getColorCode(R.color.red_DDA098),
            getColorCode(R.color.red_9B6A6C), getColorCode(R.color.purple_706677)
        )

        fun setPieChart(list: MutableList<PieEntry>) {
            val dataSet = PieDataSet(list, "label")

            dataSet.setColors(colors)

            val pieData = PieData(dataSet)
            pieData.setDrawValues(true)
            pieData.setValueTextSize(15f)
            pieData.setValueTextColor(getColorCode(R.color.black_3f3a3a))

            pieChart.setData(pieData)
            pieChart.invalidate()
            pieChart.setEntryLabelColor(getColorCode(R.color.black_3f3a3a))
            pieChart.setEntryLabelTextSize(15f)
        }


        viewModel.pieEntryList.observe(viewLifecycleOwner, {
            Log.i("Rita", "result pieEntryList observe: $it")
            setPieChart(it)
        })


        //calendar
        val collapsibleCalendar: CollapsibleCalendar = binding.calendarView
        collapsibleCalendar.setCalendarListener(object : CollapsibleCalendar.CalendarListener {
            override fun onDayChanged() {}
            override fun onClickListener() {}
            override fun onDaySelect() {
                val day = collapsibleCalendar.selectedDay
                val dateSelected = "" + day!!.day + "-" + (day.month + 1) + "-" + day.year

                viewModel.selectedTimeSet(dateSelected)

                Log.i("Rita", "result Selected Day: $dateSelected")
            }

            override fun onItemClick(v: View) {}
            override fun onDataUpdate() {}
            override fun onMonthChange() {}
            override fun onWeekChange(position: Int) {}
        })

        return binding.root

    }

}