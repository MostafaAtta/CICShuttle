package com.atta.cicshuttle.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.atta.cicshuttle.LocationReceiver
import com.atta.cicshuttle.R
import com.atta.cicshuttle.SessionManager
import com.atta.cicshuttle.model.Route
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {


    private lateinit var mMap: GoogleMap

    private lateinit var locationRequest: LocationRequest

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var db: FirebaseFirestore

    private lateinit var busMarker: Marker

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        mMap = googleMap

        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED
                && context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {

            requestPermission()
            return@OnMapReadyCallback
        }

        //mMap.isMyLocationEnabled = true
        updateLocation()

        if (context?.let { SessionManager.with(it).getRouteId() } != null ) {
            if (context?.let {SessionManager.with(it).getRouteId()} != "" &&
                context?.let {SessionManager.with(it).isEnabled()}!!) {
                getRouteDriverLocation()
            }else if (!context?.let {SessionManager.with(it).isEnabled()}!!){
                Toast.makeText(requireContext(), getString(R.string.disabled_msg), Toast.LENGTH_LONG).show()
            }else if (context?.let {SessionManager.with(it).getRouteId()} == ""){
                Toast.makeText(requireContext(), getString(R.string.route_error_msg), Toast.LENGTH_LONG).show()

            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        instance = this

        db = Firebase.firestore

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFrag) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    private fun requestPermission() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(permission, FINE_LOCATION_REQUEST_CODE)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (permissions[0] === Manifest.permission.ACCESS_FINE_LOCATION
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //mMap.setMyLocationEnabled(true)
                //fetchLocation()
            }
        }
    }

    
    fun updateLocationOnMap(location: Location) {

        //mLocation = location

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng))

        val icon2: BitmapDescriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.passenger)
        //val myLatLng2 = LatLng(30.058205443102477, 31.345448798902545)
        val myLatLng2 = LatLng(location.latitude, location.longitude)
        var marker2 = MarkerOptions().position(myLatLng2)
                //.title(routeName)
                .icon(icon2)
        mMap.addMarker(marker2)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng2, 14f))


    }

    private fun updateLocation(){
        buildLocationRequest()
        val result = context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }

        if (result != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
            return
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent())

    }

    private fun getPendingIntent(): PendingIntent {

        val intent = Intent(context, LocationReceiver::class.java)
        intent.action = LocationReceiver.ACTION_LOCATION_UPDATE
        return  PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    }

    private fun buildLocationRequest(){
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(3000)
                .setSmallestDisplacement(10f)
    }

    private fun getRouteDriverLocation(){
        val routeId = context?.let { SessionManager.with(it).getRouteId() }
        db.collection("Routes")
                .document(routeId!!)
                .get()
                .addOnSuccessListener {
                    if (it != null){
                        val route = it.toObject(Route::class.java)

                        val myLatLng = route?.driverLocation?.let { it1 -> LatLng(it1.latitude, it1.longitude) }

                        addBusMarker(myLatLng!!)
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "get failed with ", it)
                    Toast.makeText(context, "get failed with  $it", Toast.LENGTH_SHORT).show()
                }
    }

    private fun addBusMarker(latLng: LatLng){
        val icon: BitmapDescriptor = BitmapDescriptorFactory
            .fromResource(R.drawable.school_bus_marker)

        val routeName = context?.let { SessionManager.with(it).getRouteName() }

        var marker = MarkerOptions().position(latLng)
            .title(routeName)
            .icon(icon)
        busMarker = mMap.addMarker(marker)

        busLocationListener()
    }

    private fun busLocationListener(){

        val routeId = context?.let { SessionManager.with(it).getRouteId() }
        val docRef = db.collection("Routes").document(routeId!!)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                //Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val location = snapshot.data?.get("driverLocation") as GeoPoint
                val myLatLng =  LatLng(location.latitude, location.longitude)
                busMarker.position = myLatLng
                //Log.d(TAG, "Current data: ${snapshot.data}")
            }
        }
    }

    companion object {
        private const val FINE_LOCATION_REQUEST_CODE = 101

        private const val TAG = "HomeFragment"

        var instance: HomeFragment? = null

        fun getHomeInstance(): HomeFragment?{
            return instance
        }
    }
}