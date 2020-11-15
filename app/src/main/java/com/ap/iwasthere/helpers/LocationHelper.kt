package com.ap.iwasthere.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import java.util.*


/**
 * A simple helper class to fetch the last known location of the device.
 *
 * @author Gaetan Dumortier
 * @since 14 November 2020
 */
@SuppressLint("MissingPermission") // We check permissions on runtime, thus this can be ignored
class LocationHelper(private val activity: Activity) {

    private var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    lateinit var locationRequest: LocationRequest

    private var geocoder: Geocoder = Geocoder(activity.applicationContext, Locale.getDefault())
    private var location: Location? = null

    suspend fun getLocation(): String {
        var address = "Fout bij localisatie."
        val loc = getLastLocation()
        if (loc != null) {
            val addressList = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
            address = addressList[0].getAddressLine(0)
        }

        return address
    }

    private suspend fun getLastLocation(): Location? {
        if (!isLocationEnabled()) {
            Log.d("LocationHelper", "Location cant be fetched through either GPS or network.")
            return null
        }

        return fusedLocationProviderClient.lastLocation.await()
    }

    /*
    fun getLastLocation(): String {
        var locationStr = ""

        if (isLocationEnabled()) {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                val loc: Location? = task.result
                if (loc == null) {
                    Log.d("LocationHelper", "Last location is null.")
                    fetchNewLocation()
                } else {
                    this.location = loc
                }

                val addressList: List<Address> =
                    geocoder.getFromLocation(this.location!!.latitude, this.location!!.longitude, 1)
                locationStr = addressList[0].getAddressLine(0)
                Log.d("LocationHelper", "in listener: $locationStr")
            }.addOnFailureListener {
                Log.d("LocationHelper", "Failed to fetch location: " + it.message)
            }
            return locationStr
        } else {
            val intent = Intent(activity, PermissionErrorActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }
    }
    */

    private fun fetchNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1

        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            location = p0.lastLocation
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}