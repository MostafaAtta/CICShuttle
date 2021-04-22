package com.atta.cicshuttle.model

data class User(var email: String, var firstName: String, var lastName: String ,
                var phone: String){
    var id: String = ""
    constructor(): this("", "", "", "")

}
