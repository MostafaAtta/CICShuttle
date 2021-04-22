package com.atta.cicshuttle.model

data class ChatChannel(val userIds: MutableList<String>) {

    constructor(): this(mutableListOf())
}