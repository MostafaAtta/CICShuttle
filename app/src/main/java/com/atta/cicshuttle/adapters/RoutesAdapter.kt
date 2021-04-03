package com.atta.cicshuttle.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.atta.cicshuttle.R
import com.atta.cicshuttle.databinding.RoutesItemBinding
import com.atta.cicshuttle.model.Route
import com.atta.cicshuttle.ui.RoutesFragmentDirections
import java.util.*

open class RoutesAdapter (private val data: List<Route>,
                          private val activity: Activity):
    RecyclerView.Adapter<RoutesAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: RoutesItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RoutesItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val route = data[position]

        with(holder){
            with(route){
                binding.routeName.text = name
                binding.startTime.text = startTime
                binding.endTime.text = arrivalTime

            }


            binding.root.setOnClickListener {
                Navigation.findNavController(activity, R.id.nav_host_fragment)
                    .navigate(RoutesFragmentDirections.actionNavigationRoutesToRouteDetailsFragment(route))
            }
        }
    }

    override fun getItemCount(): Int {
        return  data.size
    }
}