package com.example.finedriver.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.finedriver.R
import com.example.finedriver.data.cameraData.CameraRepository
import kotlinx.android.synthetic.main.fragment_login.*


class CameraListFragment : Fragment() {
    //Здесь и далее тестовый код
    var cameraRepository =
        CameraRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var cameraList = activity?.let { cameraRepository.getStringFromJsonFile(it) }
        var list =  cameraRepository.getCamerasList(cameraList!!)
        textView.text = list.toString()
    }
}