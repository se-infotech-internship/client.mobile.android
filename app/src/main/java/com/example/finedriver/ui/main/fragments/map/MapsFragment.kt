package com.example.finedriver.ui.main.fragments.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finedriver.R
import com.example.finedriver.data.CameraRepository
import com.example.finedriver.data.model.CameraItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_maps.*


class MapsFragment : Fragment(), OnMapReadyCallback {

    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cameraRepository = CameraRepository()
    private lateinit var camerasList : List<CameraItem>
    private lateinit var camerasCoordinationList : List<LatLng>
    private lateinit var map: GoogleMap
    private var curentLon : Double? = 30.5234
    private var curentLat : Double? = 50.4494

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        my_location_button.setOnClickListener(myLocationClickListener)

    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        camerasList = cameraRepository.getCamerasList(cameraRepository.getStringFromJsonFile(requireActivity()))
        camerasCoordinationList = cameraRepository.getCamerasCoordinationList(camerasList)

        var bitmap: BitmapDescriptor?
        for (cameraItem in camerasList) {
            bitmap = if (cameraItem.state == "on") {
                generateBitmapDescriptorFromRes(activity, R.drawable.ic_map_camera_on)
            } else {
                generateBitmapDescriptorFromRes(activity, R.drawable.ic_map_camera_off)
            }
            googleMap.addMarker(MarkerOptions().position(LatLng(cameraItem.lat, cameraItem.lon)).icon(bitmap).title(cameraItem.address))
        }
        map.isTrafficEnabled = true


        getLastLocation()
        moveToMyLocation()
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

    fun generateBitmapDescriptorFromRes(
        context: Context?, resId: Int
    ): BitmapDescriptor? {
        val drawable = context?.let { ContextCompat.getDrawable(it, resId) }
        drawable!!.setBounds(
            0,
            0,
            drawable.intrinsicWidth,
            drawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        enableMyLocation()
        fusedLocationClient.lastLocation
            .addOnCompleteListener { taskLocation ->
                val location = taskLocation.result
                curentLat = location?.latitude
                curentLon = location?.longitude
            }
    }


    private val myLocationClickListener: View.OnClickListener = View.OnClickListener { view ->
        getLastLocation()
        moveToMyLocation()
    }

    private fun moveToMyLocation() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curentLon?.let {
            curentLat?.let { it1 ->
                LatLng(
                    it1, it
                )
            }
        }, 16f))
    }
}