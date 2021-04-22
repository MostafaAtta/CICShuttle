package com.atta.cicshuttle.adapters

import androidx.recyclerview.widget.DiffUtil
import com.atta.cicshuttle.model.Message

class MyDiffUtilCallback(private val oldList: List<Message>,
                         private val newList: List<Message>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldListSize
    }

    override fun getNewListSize(): Int {
        return newListSize
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList == newList
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}