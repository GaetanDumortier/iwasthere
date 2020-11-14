package com.ap.iwasthere.helpers

import android.annotation.SuppressLint
import android.app.Activity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission") // We check permissions on runtime, thus this warning can be ignored.
class LocationHelper(activity: Activity) {
    // TODO: implement IP address helper
    // HTTP GET call with provided IP to extract
    // https://ip-api.com/

    private var fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    fun getLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {

        }
    }

}