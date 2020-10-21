package com.example.finedriver.ui.main.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.finedriver.R
import kotlinx.android.synthetic.main.fragment_inspection_settings.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.fines_list_back_button

class InspectionSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_inspection_settings, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        every_day_layout.setOnClickListener(everyDaySettingsClickListener)
        every_week_layout.setOnClickListener(everyWeekSettingsClickListener)
        every_month_layout.setOnClickListener(everyMonthSettingsClickListener)
        every_year_layout.setOnClickListener(everyYearSettingsClickListener)
        never_layout.setOnClickListener(neverSettingsClickListener)
        fines_list_back_button.setOnClickListener(backClickListener)
        every_week_image.visibility = View.VISIBLE
    }


    private val everyDaySettingsClickListener: View.OnClickListener = View.OnClickListener { view ->
        every_day_image.visibility = View.VISIBLE
        every_week_image.visibility = View.INVISIBLE
        every_month_image.visibility = View.INVISIBLE
        every_year_image.visibility = View.INVISIBLE
        never_image.visibility = View.INVISIBLE
    }
    private val everyWeekSettingsClickListener: View.OnClickListener = View.OnClickListener { view ->
        every_day_image.visibility = View.INVISIBLE
        every_week_image.visibility = View.VISIBLE
        every_month_image.visibility = View.INVISIBLE
        every_year_image.visibility = View.INVISIBLE
        never_image.visibility = View.INVISIBLE
    }
    private val everyMonthSettingsClickListener: View.OnClickListener = View.OnClickListener { view ->
        every_day_image.visibility = View.INVISIBLE
        every_week_image.visibility = View.INVISIBLE
        every_month_image.visibility = View.VISIBLE
        every_year_image.visibility = View.INVISIBLE
        never_image.visibility = View.INVISIBLE
    }
    private val everyYearSettingsClickListener: View.OnClickListener = View.OnClickListener { view ->
        every_day_image.visibility = View.INVISIBLE
        every_week_image.visibility = View.INVISIBLE
        every_month_image.visibility = View.INVISIBLE
        every_year_image.visibility = View.VISIBLE
        never_image.visibility = View.INVISIBLE
    }
    private val neverSettingsClickListener: View.OnClickListener = View.OnClickListener { view ->
        every_day_image.visibility = View.INVISIBLE
        every_week_image.visibility = View.INVISIBLE
        every_month_image.visibility = View.INVISIBLE
        every_year_image.visibility = View.INVISIBLE
        never_image.visibility = View.VISIBLE
    }
    private val backClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_inspectionSettingsFragment_to_finesSettingsFragment);
    }


}