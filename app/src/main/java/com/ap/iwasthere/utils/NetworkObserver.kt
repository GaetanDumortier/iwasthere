package com.ap.iwasthere.utils

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.ap.iwasthere.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase

/**
 * A class responsible for registering a network callback and observer
 * which will, in realtime, provide the current state of connectivity for the device.
 *
 * This way we can display an error when the device is not connected to the internet.
 *
 * @param context the context of the application
 */
@Suppress("DEPRECATION")
class NetworkObserver(private val context: Context) : LiveData<Boolean>() {

    /**
     * Start an observer to check network status of the device.
     *
     * @param view the view to send actions to
     * @param owner the owner of the observer lifecycle
     */
    fun observe(view: View, owner: LifecycleOwner) {
        val networkConnection = NetworkObserver(context)
        var snackbar: Snackbar? = null

        networkConnection.observe(owner, Observer { isConnected ->
            if (!isConnected) {
                snackbar = Snackbar.make(
                    view,
                    R.string.snackbar_text,
                    BaseTransientBottomBar.LENGTH_INDEFINITE
                )
                snackbar?.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                val layout = snackbar?.view as Snackbar.SnackbarLayout
                layout.minimumWidth = view.width

                snackbar?.show()
                FirebaseDatabase.getInstance().goOnline()
                // UIUtils().enableAllInput(view, false)
            } else {
                snackbar?.dismiss()
                //UIUtils().enableAllInput(view, true)
            }
        })
    }

    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun onActive() {
        super.onActive()
        updateConnection()

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                connectivityManager.registerDefaultNetworkCallback(networkCallback())
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                lollipopNetworkRequest()
            }
            else -> {
                context.registerReceiver(
                    networkReceiver,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        // Check if we have any active observers to unregister
        if (hasActiveObservers()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                connectivityManager.unregisterNetworkCallback(networkCallback())

            } else {
                context.unregisterReceiver(networkReceiver)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun lollipopNetworkRequest() {
        val requestBuilder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        connectivityManager.registerNetworkCallback(
            requestBuilder.build(),
            networkCallback()
        )
    }

    private fun networkCallback(): ConnectivityManager.NetworkCallback {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    super.onLost(network)
                    postValue(false)
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    postValue(true)
                }
            }
            return networkCallback as ConnectivityManager.NetworkCallback

        } else {
            throw IllegalAccessError("Error. Invalid build")
        }
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateConnection()
        }
    }

    private fun updateConnection() {
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnected == true)
    }
}