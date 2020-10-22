package com.example.finedriver.ui.main.fragments.fines

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finedriver.R
import kotlinx.android.synthetic.main.fragment_full_fines_ayout.*
import org.angmarch.views.NiceSpinner
import org.angmarch.views.OnSpinnerItemSelectedListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class FinesFragment : Fragment() {

    private var columnCount = 1
    private val finesViewModel = FinesViewModel()
    @RequiresApi(Build.VERSION_CODES.O)
    private var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    @RequiresApi(Build.VERSION_CODES.O)
    private val currentDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        //initRecyclerView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_full_fines_ayout, container, false)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(fines_list) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(requireContext())
                else -> GridLayoutManager(requireContext(), columnCount)
            }
            adapter = MyItemRecyclerViewAdapter(finesViewModel.getFinesList(requireContext()),
                { goToPay() })
        }


        val niceSpinner = nice_spinner as NiceSpinner
        val dataset: List<String> = listOf("ЗА ДЕНЬ", "ЗА ТИЖДЕНЬ", "ЗА МІСЯЦЬ", "ЗА РІК", "ЗА ВЕСЬ ЧАС")
        niceSpinner.attachDataSource(dataset)

        niceSpinner.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener { parent, view, position, id -> // This example uses String, but your type can be any
                val item = parent.getItemAtPosition(position)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (position == 0) {
                        fines_data_textView.text = currentDate.format(formatter).toString()
                    }
                    else if (position == 1) {
                        val dayStart = currentDate.minusWeeks(1).format(formatter)
                        fines_data_textView.text = dayStart.toString() +" - " + currentDate.format(formatter).toString()
                    }
                    else if (position == 2) {
                        val dayStart = currentDate.minusMonths(1).format(formatter)
                        fines_data_textView.text = dayStart.toString() +" - " + currentDate.format(formatter).toString()
                    }
                    else if (position == 3) {
                        val dayStart = currentDate.minusYears(1).format(formatter)
                        fines_data_textView.text = dayStart.toString() +" - " +currentDate.format(formatter).toString()
                    }
                    else if (position == 4) {
                        fines_data_textView.text = "ЗА ВЕСЬ ЧАС"
                    }
                }

            }




        findFinesButton.setOnClickListener(findFinesClickListener)
    }



    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            FinesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    private val findFinesClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_finesFragment_to_findFinesFragment)
    }

    fun goToPay(){
        findNavController().navigate(R.id.action_finesFragment_to_webFragment)
    }
}