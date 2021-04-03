package com.atta.cicshuttle.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.atta.cicshuttle.adapters.RoutesAdapter
import com.atta.cicshuttle.databinding.FragmentRoutesBinding
import com.atta.cicshuttle.model.Route
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RoutesFragment : Fragment() {

    private var _binding: FragmentRoutesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    var routes: ArrayList<Route> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoutesBinding.inflate(inflater, container, false)
        val view = binding.root

        db = Firebase.firestore

        getRouts()

        binding.swipeLayout.setOnRefreshListener {
            routes.clear()
            getRouts()
            binding.swipeLayout.isRefreshing = false
        }

        return view
    }

    private fun getRouts() {
        db.collection("Routes")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (document in it){
                        val route = document.toObject(Route::class.java)
                        route.id = document.id
                        routes.add(route)

                        //addRoute(route)
                    }
                    //checklists = it.toObjects<Checklist>()
                    showRecycler()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }

    }

    private fun addRoute(route: Route) {
        db.collection("Routes")
                .add(route)
                .addOnSuccessListener {

                }
                .addOnFailureListener {

                }
    }

    private fun showRecycler() {
        val routesAdapter = activity?.let { RoutesAdapter(routes, it) }

        binding.routesRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.routesRecycler.adapter = routesAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}