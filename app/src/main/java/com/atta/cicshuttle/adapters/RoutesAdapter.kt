package com.atta.cicshuttle.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.atta.cicshuttle.R
import com.atta.cicshuttle.databinding.RoutesItemBinding
import com.atta.cicshuttle.model.Route
import com.atta.cicshuttle.ui.RoutesFragmentDirections
import java.util.*

open class RoutesAdapter (private val data: ArrayList<Route>,
                          private val activity: Activity):
    RecyclerView.Adapter<RoutesAdapter.MyViewHolder>(), Filterable {

    var routesFilterList = ArrayList<Route>()
    var routesFullList = ArrayList<Route>()

    init {
        routesFilterList = data
        routesFullList = ArrayList<Route>(data)
    }

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
                binding.startTime.text = morningTimes["startTime"]
                binding.endTime.text = afternoonTimes["startTime"]

            }


            binding.root.setOnClickListener {
                Navigation.findNavController(activity, R.id.nav_host_fragment)
                    .navigate(RoutesFragmentDirections.actionNavigationRoutesToRouteDetailsFragment(route))
            }
        }
    }

    @ExperimentalStdlibApi
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var filteredList = ArrayList<Route>()
                if (constraint == null || constraint.isEmpty()) {
                    filteredList = routesFullList
                } else {
                    val charSearch = constraint.toString().toLowerCase(Locale.ROOT).trim()
                    for (row in data) {
                        if (row.name.lowercase(Locale.ROOT).contains(charSearch)) {
                            filteredList.add(row)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                routesFilterList.clear()
                routesFilterList.addAll(results?.values as ArrayList<Route>)
                notifyDataSetChanged()
            }

        }
    }

    override fun getItemCount(): Int {
        return  routesFilterList.size
    }
}