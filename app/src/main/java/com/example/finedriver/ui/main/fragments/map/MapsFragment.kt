package com.example.finedriver.ui.main.fragments.map

/*import com.example.finedriver.data.routeData.RetrofitClient*/

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finedriver.R
import com.example.finedriver.data.cameraData.CameraRepository
import com.example.finedriver.data.cameraData.model.CameraItem
import com.example.finedriver.data.routeData.RetrofitClient
import com.example.finedriver.data.routeData.model.DirectionResponses
import com.example.finedriver.servise.LocationUpdateService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.fragment_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class MapsFragment : Fragment(), OnMapReadyCallback {

    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cameraRepository = CameraRepository()
    private lateinit var camerasList : List<CameraItem>
    private lateinit var map: GoogleMap
    private var currentLon : Double = 30.5234
    private var currentLat : Double = 50.4494
    private var userSpeed : String = "0 км/г"

    private var destinationAddress : String = ""
    private var destinationLon : Double = 0.0
    private var destinationLat : Double = 0.0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!
        if (MapUtils.requestingLocationUpdates(activity)) {
            if (!isPermissionGranted()) {
                enableMyLocation()
            }
        }

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
        //val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().add(R.id.mapContainer, mapFragment).commit()
        mapFragment?.getMapAsync(this)


        my_location_button.setOnClickListener(myLocationClickListener)
        find_location_button.setOnClickListener(findLocationClickListener)
        to_menu_button.setOnClickListener(toMenuButtonClickListener)

        find_location_searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                destinationAddress = find_location_searchView.query.toString()
                getCoordinationByAddress(destinationAddress)
                find_location_searchView.visibility = View.INVISIBLE
                find_location_searchView.setQuery("", false)
                find_location_button.show()
                if (destinationLat!=0.0) {
                    map.clear()
                    drawCamerasOnMap(map)
                    map.isTrafficEnabled = false
                    val currentLocation = "$currentLat,$currentLon"
                    val destination = "$destinationLat,$destinationLon"

                    buildRoute(currentLocation, destination)
                }

                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        drawCamerasOnMap(map)

        map.isTrafficEnabled = true

        getLastLocation()
        moveToMyLocation()

        map.setOnMapClickListener(
            object : OnMapClickListener {
                override fun onMapClick(latLng: LatLng) {
                    map.clear()
                    drawCamerasOnMap(map)
                    map.isTrafficEnabled = false
                    val currentLocation = "$currentLat,$currentLon"
                    val destination =  latLng.latitude.toString() + "," +latLng.longitude.toString()

                    buildRoute(currentLocation, destination)
                }
            })
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

/*    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }*/


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        enableMyLocation()
        fusedLocationClient.lastLocation
            .addOnCompleteListener { taskLocation ->
                val location = taskLocation.result
                if (location!=null) {
                    currentLat = location.latitude
                    currentLon = location.longitude
                    /*userSpeed = (location.getSpeed()*3.6).toString()*/

                }
            }
    }


    private val myLocationClickListener: View.OnClickListener = View.OnClickListener { view ->
        getLastLocation()
        moveToMyLocation()
    }

    private val findLocationClickListener: View.OnClickListener = View.OnClickListener { view ->
        if (find_location_searchView.visibility != View.VISIBLE) {
            find_location_searchView.visibility = View.VISIBLE
            find_location_button.hide()
        }

    }

    private fun buildRoute(currentLocation: String, destination: String) {
        map.addMarker(
            MarkerOptions()
                .position(LatLng(destinationLat, destinationLon))
                .title(destinationAddress)
        )

        val apiServices = RetrofitClient.directionApiServices
        apiServices.getDirection(currentLocation, destination, getString(R.string.google_maps_key))
            .enqueue(object : Callback<DirectionResponses> {
                override fun onResponse(
                    call: Call<DirectionResponses>,
                    response: Response<DirectionResponses>
                ) {
                    drawPolyline(response)
                    Log.d("Все отлично!", response.message())
                }

                override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                    Log.e("Все плохо!!!!", t.localizedMessage)
                }
            })
    }

    private val toMenuButtonClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_mapsFragment_to_mainMenyFragment)
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
                val toast = Toast.makeText(activity, "Адреса не знайдена.", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP,0,20)
                toast.show()
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

    private fun drawCamerasOnMap(googleMap:GoogleMap){
        camerasList = cameraRepository.getCamerasList(cameraRepository.getStringFromJsonFile(requireContext()))

        var bitmap: BitmapDescriptor?
        for (cameraItem in camerasList) {
            bitmap = if (cameraItem.state == "on") {
                MapUtils.generateBitmapDescriptorFromRes(activity, R.drawable.ic_map_camera_on)
            } else {
                MapUtils.generateBitmapDescriptorFromRes(activity, R.drawable.ic_map_camera_off)
            }
            googleMap.addMarker(MarkerOptions().position(LatLng(cameraItem.lat, cameraItem.lon)).icon(bitmap).title(cameraItem.address + "  Обмеження: "+ cameraItem.speed))
        }
    }


}