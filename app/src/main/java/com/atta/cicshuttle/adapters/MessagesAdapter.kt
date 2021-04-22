package com.atta.cicshuttle.adapters

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.atta.cicshuttle.R
import com.atta.cicshuttle.SessionManager
import com.atta.cicshuttle.databinding.MessageItemBinding
import com.atta.cicshuttle.databinding.RoutesItemBinding
import com.atta.cicshuttle.model.Message
import com.atta.cicshuttle.ui.RoutesFragmentDirections
import java.text.SimpleDateFormat
import java.util.*

open class MessagesAdapter (private val data: List<Message>,
                            private val activity: Activity):
    RecyclerView.Adapter<MessagesAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: MessageItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MessageItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = data[position]

        with(holder){
            with(message){
                if (senderId == SessionManager.with(activity).getUserId()) {
                    binding.root.apply {
                        binding.backgroundLy.setBackgroundResource(R.drawable.rect_round_white)
                        val lParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.END)
                        this.layoutParams = lParams
                    }
                }
                else {
                    binding.root.apply {
                        binding.backgroundLy.setBackgroundResource(R.drawable.rect_round_primary_color)
                        val lParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.START)
                        this.layoutParams = lParams
                    }
                }
                binding.textViewMessageText.text = text
                val dateFormat = SimpleDateFormat
                        .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
                binding.textViewMessageTime.text = dateFormat.format(time.toDate())
            }

        }
    }

    override fun getItemCount(): Int {
        return  data.size
    }
}