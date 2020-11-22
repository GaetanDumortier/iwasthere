package com.ap.iwasthere.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * A class describing the actions and properties of a location.
 *
 * @author Gaetan Dumortier
 * @since 19 November 2020
 */
class Location() : Parcelable {
    var id: String? = null

    var locality: String? = null

    var postalCode: String? = null

    var address: String? = null

    /**
     * Constructor.
     *
     * @param id the unique identifier of the student
     * @param locality the locality of the location (eg.: Antwerpen)
     * @param postalCode the postalcode of the location
     * @param address the full address of the location
     */
    constructor(id: String, locality: String?, postalCode: String?, address: String?) : this() {
        this.id = id
        this.locality = locality
        this.postalCode = postalCode
        this.address = address
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        locality = parcel.readString()
        postalCode = parcel.readString()
        address = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(locality)
        dest?.writeString(postalCode)
        dest?.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Location> {
        override fun createFromParcel(parcel: Parcel): Location {
            return Location(parcel)
        }

        override fun newArray(size: Int): Array<Location?> {
            return arrayOfNulls(size)
        }
    }

    fun makeLocation(locality: String?, postalCode: String?, address: String?): Location {
        return Location(UUID.randomUUID().toString(), locality, postalCode, address)
    }
}