package com.atta.cicshuttle.model

data class Stop(var type: String, var name: String, var time: Int){
    var id: String = ""
    constructor(): this("", "", 0)

}
