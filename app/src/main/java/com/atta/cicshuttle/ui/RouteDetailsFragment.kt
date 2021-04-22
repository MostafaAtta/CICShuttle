package com.atta.cicshuttle.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.atta.cicshuttle.databinding.FragmentRouteDetailsBinding
import com.atta.cicshuttle.model.Route
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RouteDetailsFragment : Fragment() {

    private var _binding: FragmentRouteDetailsBinding? = null
    private val binding get() = _binding!!

    lateinit var db: FirebaseFirestore

    lateinit var route: Route

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRouteDetailsBinding.inflate(inflater, container, false)

        route = arguments?.let { RouteDetailsFragmentArgs.fromBundle(it).route }!!

        //Toast.makeText(context, route?.name, Toast.LENGTH_SHORT).show()

        db = Firebase.firestore
        locationListener()
        return binding.root
    }

    private fun locationListener(){
        val docRef = db.collection("Drivers").document(route.driverId)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                //Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val location = snapshot.data?.get("location") as GeoPoint
                Toast.makeText(context, "${location.latitude} , ${location.longitude}", Toast.LENGTH_SHORT).show()

                //Log.d(TAG, "Current data: ${snapshot.data}")
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

}