package com.example.finedriver.ui.main.fragments.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.finedriver.R
import com.example.finedriver.data.CameraRepository
import com.example.finedriver.data.model.CameraItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment(), OnMapReadyCallback {

    private val REQUEST_LOCATION_PERMISSION = 1

    private var cameraRepository = CameraRepository()
    private lateinit var camerasList : List<CameraItem>
    private lateinit var camerasCoordinationList : List<LatLng>
    private lateinit var map: GoogleMap




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        camerasList = cameraRepository.getCamerasList(cameraRepository.getStringFromJsonFile(requireActivity()))
        camerasCoordinationList = cameraRepository.getCamerasCoordinationList(camerasList)

        val callback = OnMapReadyCallback { googleMap ->
            val kiev = LatLng(50.45021188, 30.52427083)
            googleMap.addMarker(MarkerOptions().position(kiev).title("Marker in Kiev"))

            for (cameraItem in camerasList) {
                googleMap.addMarker(MarkerOptions().position(LatLng(cameraItem.lat, cameraItem.lon)).icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_GREEN)).title(cameraItem.address))
            }

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kiev,12f))

        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        mapFragment?.getMapAsync(this)

    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
    }


    private fun isPermissionGranted() : Boolean {
        return activity?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION)
        } == PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        }
        else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }
}