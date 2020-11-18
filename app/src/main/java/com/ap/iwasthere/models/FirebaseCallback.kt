package com.ap.iwasthere.models

/**
 * A callback interface which will be used to handle
 * async operations with the Firebase database.
 *
 * @author Gaetan Dumortier
 * @since 15 November 2015
 */
class FirebaseCallback {
    /**
     * Interface to handle List callbacks.
     */
    interface ListCallback {
        /**
         * Callback when handling a list
         *
         * @param value a list of any objects
         */
        fun onListCallback(value: List<Any>)
    }


    /**
     * Interface to handle single object callbacks.
     */
    interface ItemCallback {
        /**
         * Callback when handling one specific object
         *
         * @param value: the any object
         */
        fun onItemCallback(value: Any)
    }
}