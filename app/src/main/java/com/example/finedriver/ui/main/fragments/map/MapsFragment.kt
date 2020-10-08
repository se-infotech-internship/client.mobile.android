package com.example.finedriver.ui.main.fragments.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.finedriver.R
import com.example.finedriver.data.cameraData.CameraRepository
import com.example.finedriver.data.cameraData.model.CameraItem
import com.example.finedriver.data.routeData.RetrofitClient
/*import com.example.finedriver.data.routeData.RetrofitClient*/
import com.example.finedriver.data.routeData.model.DirectionResponses
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.fragment_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException


class MapsFragment : Fragment(), OnMapReadyCallback {

    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cameraRepository =
        CameraRepository()
    private lateinit var camerasList : List<CameraItem>
    private lateinit var camerasCoordinationList : List<LatLng>
    private lateinit var map: GoogleMap
    private var currentLon : Double = 30.5234
    private var currentLat : Double = 50.4494

    private var destinationAddress : String = ""
    private var destinationLon : Double = 0.0
    private var destinationLat : Double = 0.0

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
        find_location_button.setOnClickListener(findLocationClickListener)

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
        /*map.isTrafficEnabled = true*/

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
            map.uiSettings.isMyLocationButtonEnabled = false
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
                if (location!=null) {
                    currentLat = location.latitude
                    currentLon = location.longitude
                }
            }
    }


    private val myLocationClickListener: View.OnClickListener = View.OnClickListener { view ->
        getLastLocation()
        moveToMyLocation()
    }

    private val findLocationClickListener: View.OnClickListener = View.OnClickListener { view ->
        if (find_location_textView.visibility == View.VISIBLE) {
            find_location_textView.visibility = View.INVISIBLE

            if (find_location_textView.text != null) {
                destinationAddress = find_location_textView.text.toString()
                find_location_textView.text.clear()
                getCoordinationByAddress(destinationAddress)

                if (destinationLat==0.0) {
                    val currentLocation = "$currentLat,$currentLon"
                    val destination = "$destinationLat,$destinationLon"

                    map.addMarker(MarkerOptions()
                        .position(LatLng(destinationLat,destinationLon))
                        .title(destinationAddress))

                    val apiServices = RetrofitClient.directionApiServices
                    apiServices.getDirection(currentLocation, destination, getString(R.string.google_maps_key))
                        .enqueue(object : Callback<DirectionResponses> {
                            override fun onResponse(call: Call<DirectionResponses>, response: Response<DirectionResponses>) {
                                drawPolyline(response)
                                Log.d("Все отлично!", response.message())
                            }

                            override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                                Log.e("Все плохо!!!!", t.localizedMessage)
                            }
                        })
                }
            }
        }
        else{
            find_location_textView.visibility = View.VISIBLE
        }
    }

    private fun moveToMyLocation() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLon.let {
            currentLat.let { it1 ->
                LatLng(
                    it1, it
                )
            }
        }, 16f))
    }

    private fun getCoordinationByAddress(address: String) {
        val geocoder = Geocoder(activity)
        val addresses: List<Address>?

        try {
            addresses = geocoder.getFromLocationName(address, 1)

            if (addresses.isNotEmpty()) {
                val addr: Address = addresses.get(0)
                destinationLon = addr.longitude
                destinationLat = addr.latitude
            }
            else{
                Toast.makeText(activity, "Адреса не знайдена.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }
    }


    private fun drawPolyline(response: Response<DirectionResponses>) {
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        val polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(16f)
            .color(Color.BLUE)
        map.addPolyline(polyline)
    }

}