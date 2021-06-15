package com.atta.cicshuttle.ui

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.atta.cicshuttle.R
import com.atta.cicshuttle.SessionManager
import com.atta.cicshuttle.databinding.FragmentRouteDetailsBinding
import com.atta.cicshuttle.model.Point
import com.atta.cicshuttle.model.Route
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RouteDetailsFragment : Fragment() {

    private var _binding: FragmentRouteDetailsBinding? = null
    private val binding get() = _binding!!

    lateinit var db: FirebaseFirestore

    lateinit var route: Route

    private lateinit var mMap: GoogleMap

    lateinit var mLocation: Location

    private lateinit var busMarker: Marker


    private lateinit var mapFragment: SupportMapFragment

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        addBusMarker(route.driverLocation)

        getPoints()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRouteDetailsBinding.inflate(inflater, container, false)

        mapFragment = childFragmentManager.findFragmentById(R.id.mapFrag) as SupportMapFragment

        route = arguments?.let { RouteDetailsFragmentArgs.fromBundle(it).route }!!

        //Toast.makeText(context, route?.name, Toast.LENGTH_SHORT).show()

        db = Firebase.firestore

        initiateViews()

        return binding.root
    }

    private fun getPoints(){
        db.collection("Routes")
                .document(route.id)
                .collection("Points")
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty){
                        for (document in it){
                            val point = document.toObject(Point::class.java)
                            point.id = document.id
                            addPointMarker(point)
                        }

                    }
                }
                .addOnFailureListener{

                }
    }

    private fun addPointMarker(point: Point) {
        val latLng = LatLng(point.location.latitude, point.location.longitude)

        var marker = MarkerOptions().position(latLng)
                .title(point.name)

        mMap.addMarker(marker)
    }

    private fun busLocationListener(){

        val docRef = db.collection("Routes").document(route.id)
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

    private fun getRouteDriverLocation(){

        db.collection("Routes")
                .document(route.id)
                .get()
                .addOnSuccessListener {
                    if (it != null){
                        val route = it.toObject(Route::class.java)

                        val myLatLng = route?.driverLocation?.let { it1 -> LatLng(it1.latitude, it1.longitude) }

                        //addBusMarker(myLatLng!!)
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "get failed with ", it)
                    Toast.makeText(context, "get failed with  $it", Toast.LENGTH_SHORT).show()
                }
    }

    private fun addBusMarker(busLocation: GeoPoint){

        val latLng = LatLng(busLocation.latitude, busLocation.longitude)
        val icon: BitmapDescriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.school_bus_marker)

        var marker = MarkerOptions().position(latLng)
                .title(route.name)
                .icon(icon)
        busMarker = mMap.addMarker(marker)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f))

        busLocationListener()
    }

    companion object {

        private const val TAG = "RouteDetailsFragment"

        var instance: RouteDetailsFragment? = null

        fun getHomeInstance(): RouteDetailsFragment?{
            return instance
        }
    }


    private fun initiateViews(){
        if (context?.let { SessionManager.with(it).getRouteId() } == route.id){
            binding.joinBtn.visibility = View.GONE

            mapFragment.view?.visibility = View.VISIBLE

            mapFragment.getMapAsync(callback)

        }else{

            binding.joinBtn.visibility = View.VISIBLE

            mapFragment.view?.visibility = View.GONE

            binding.joinBtn.setOnClickListener {
                addRequest()
            }
        }
        binding.busNameTxt.text = route.name
        binding.morningStartTxt.text = route.morningTimes["startTime"]
        binding.morningEndTxt.text = route.morningTimes["endTime"]
        binding.afternoonStartTxt.text = route.afternoonTimes["startTime"]
        binding.afternoonEndTxt.text = route.afternoonTimes["endTime"]
        binding.capacityTxt.text = route.busCapacity.toString()
        binding.driverNameTxt.text = route.driverName

    }

    private fun addRequest() {
        val request = mapOf("routeId" to route.id,
                "userId" to context?.let { SessionManager.with(it).getUserId()},
                    "routeName" to route.name,
                    "userName" to context?.let { SessionManager.with(it).getUserName()},
                    "status" to "New"
                )
        db.collection("Route requests")
                .document()
                .set(request)
                .addOnSuccessListener {
                    Toast.makeText(context, "Request added", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }

    }

}