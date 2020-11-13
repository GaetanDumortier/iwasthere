package com.ap.iwasthere.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

/**
 * A class describing the actions and properties of a student's registered signatures
 *
 * @author Gaetan Dumortier
 * @since 13 November 2020
 */
class Signature() : Parcelable {
    var id: String? = null
        get() = field
        set(value) {
            field = value
        }
    var date: String? = null
        get() = field
        set(value) {
            field = value
        }
    var location: String? = null
        get() = field
        set(value) {
            field = value
        }
    var signatureEncoded: String? = null
        get() = field
        set(value) {
            field = value
        }
    var studentId: String? = null
        get() = field
        set(value) {
            field = value
        }

    /**
     * Constructor.
     *
     * @param id the unique identifier of the signature
     * @param date the date the signature was placed
     * @param location the location where the registration happened
     * @param signature the signature itself
     * @param studentId the unique identifier of the student
     */
    constructor(id: String, date: String, location: String, signature: String, studentId: String) : this() {
        this.id = id
        this.date = date
        this.location = location
        this.signatureEncoded = signature
        this.studentId = studentId
    }

    @RequiresApi(Build.VERSION_CODES.M)
    constructor(parcel: Parcel) : this() {
        this.id = parcel.readString()
        this.date = parcel.readString()
        this.location = parcel.readString()
        this.signatureEncoded = parcel.readString()
        this.studentId = parcel.readString()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(date)
        dest.writeString(location)
        dest.writeString(signatureEncoded)
        dest.writeString(studentId)
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