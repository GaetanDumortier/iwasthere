package com.ap.iwasthere.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.util.*

/**
 * A class describing the actions and properties of a student's registered signatures
 *
 * @author Gaetan Dumortier
 * @since 13 November 2020
 */
class StudentSignatures() : Parcelable {
    var id: String? = null
        get() = field
        set(value) {
            field = value
        }
    var date: Date? = null
        get() = field
        set(value) {
            field = value
        }
    var signature: String? = null
        get() = field
        set(value) {
            field = value
        }
    var location: String? = null
        get() = field
        set(value) {
            field = value
        }

    /**
     * Constructor.
     *
     * @param id the unique identifier of the signature
     * @param date the date the signature was placed
     * @param signature the signature itself
     * @param location the location where the registration happened
     */
    constructor(id: String, date: Date, signature: String, location: String) : this() {
        this.id = id
        this.date = date
        this.signature = signature
        this.location = location
    }

    @RequiresApi(Build.VERSION_CODES.M)
    constructor(parcel: Parcel) : this() {
        this.id = parcel.readString()
        this.date = Date(parcel.readLong())
        this.signature = parcel.readString()
        this.location = parcel.readString()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeLong(date!!.time)
        dest.writeString(signature)
        dest.writeString(location)
    }

    override fun describeContents(): Int {
        return 0;
    }

    companion object CREATOR : Parcelable.Creator<Student> {
        override fun createFromParcel(parcel: Parcel): Student {
            return Student(parcel)
        }

        override fun newArray(size: Int): Array<Student?> {
            return arrayOfNulls(size)
        }
    }
}