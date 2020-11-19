package com.ap.iwasthere.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.Base64
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
    var signature: String? = null
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
     * @param signature the signature itself, encoded as a base64 string
     * @param studentId the unique identifier of the student
     */
    constructor(id: String, date: String, location: String, signature: String, studentId: String) : this() {
        this.id = id
        this.date = date
        this.location = location
        this.signature = signature
        this.studentId = studentId
    }

    @RequiresApi(Build.VERSION_CODES.M)
    constructor(parcel: Parcel) : this() {
        this.id = parcel.readString()
        this.date = parcel.readString()
        this.location = parcel.readString()
        this.signature = parcel.readString()
        this.studentId = parcel.readString()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(date)
        dest.writeString(location)
        dest.writeString(signature)
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

    /**
     * Decode the base64 string of the signature to a bitmap image.
     *
     * @return the bitmap image or null if decoding failed
     */
    fun decodeSignature(): Bitmap {
        val imageBytes = Base64.decode(this.signature, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}