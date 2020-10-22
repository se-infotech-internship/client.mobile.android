package com.example.finedriver.ui.main.fragments.fines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finedriver.R
import kotlinx.android.synthetic.main.fragment_find_fines.*
import kotlinx.android.synthetic.main.fragment_main_menu.*

class FindFinesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_find_fines, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findFinesButton.setOnClickListener(finesClickListener)
        fines_list_back_button.setOnClickListener(finesClickListener)

    }



    private val finesClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_findFinesFragment_to_finesFragment)
    }
}