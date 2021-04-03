package com.atta.cicshuttle.model

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class Route(var driverId: String, var driverName: String, var name: String, var arrivalTime: String,
                 var startTime: String, var driverLocation: GeoPoint): Serializable{
    var id: String = ""
    constructor(): this("", "", "", "", "", GeoPoint(0.0, 0.0))

}
