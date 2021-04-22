package com.atta.cicshuttle.model

import com.google.firebase.Timestamp
import java.util.*

data class Message(val text: String,
                   val time: Timestamp,
                   val senderId: String) {

    constructor() : this("", Timestamp(Date(0)), "")
}