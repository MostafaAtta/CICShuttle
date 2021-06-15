package com.atta.cicshuttle.model

data class User(var email: String, var firstName: String, var lastName: String ,
                var phone: String, var tokens: MutableList<String>,
                var collegeId: String, var enabled: Boolean){
    var id: String = ""
    constructor(): this("", "", "", "", mutableListOf(),
            "", false)

}
