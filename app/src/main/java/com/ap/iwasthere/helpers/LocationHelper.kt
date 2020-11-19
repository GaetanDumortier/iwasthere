package com.ap.iwasthere.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log

import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import kotlinx.coroutines.tasks.await

import java.util.*

import com.ap.iwasthere.models.Location as LocationModel


/**
 * A simple helper class to fetch the last known location of the device.
 *
 * @author Gaetan Dumortier
 * @since 14 November 2020
 */
@SuppressLint("MissingPermission") // We check permissions on runtime, thus this can be ignored
class LocationHelper(private val activity: Activity) {
    private var TAG: String = "LocationHelper"

    private var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    lateinit var locationRequest: LocationRequest

    private var geocoder: Geocoder = Geocoder(activity.applicationContext, Locale.getDefault())
    private var location: Location? = null

    suspend fun getLocation(): LocationModel {
        var location: LocationModel? = null
        val loc = getLastLocation()
        if (loc != null) {
            val addressList = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
            val address: Address = addressList[0]
            location = LocationModel().makeLocation(address.locality, address.postalCode, address.getAddressLine(0))
        }

        return location!!
    }

    private suspend fun getLastLocation(): Location? {
        if (!isLocationEnabled()) {
            Log.d(TAG, "Location cant be fetched through either GPS or network provider.")
            return null
        }

        return fusedLocationProviderClient.lastLocation.await()
    }

    /*
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
     */

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

    companion object {
        // An array of valid streets which the location should contain
        private var validStreets = arrayOf("Ellermanstraat", "Italiëlei", "Noorderplaats")

        /**
         * Check if a provided location contains a certain address/street.
         * If not, it is marked as suspicious.
         */
        fun locationIsSuspicious(location: LocationModel): Boolean {
            validStreets.forEach {
                return !location.address!!.contains(it)
            }

            return false
        }
    }
}