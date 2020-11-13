package com.ap.iwasthere.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*
import kotlin.collections.ArrayList

/**
 * A class describing the actions and properties of the student class.
 *
 * Using the Parcelable interface instead of Serializable as
 * this is a lot faster and requires less garbage collection.
 *
 * @author Gaetan Dumortier
 * @since 12 November 2020
 */
class Student() : Parcelable {
    var id: String? = null
        get() = field
        set(value) {
            field = value
        }
    var firstName: String? = null
        get() = field
        set(value) {
            field = value
        }
    var lastName: String? = null
        get() = field
        set(value) {
            field = value
        }
    var signatures: ArrayList<StudentSignatures> = ArrayList()
        get() = field
        set(value) {
            field = value
        }
    var fullName: String? = null
        get() = field
        set(value) {
            field = """$firstName $lastName"""
        }

    /**
     * Constructor.
     *
     * @param id the unique identifier of the student
     * @param firstName the first name of the student
     * @param lastName the last name of the student
     */
    constructor(id: String, firstName: String?, lastName: String?) : this() {
        this.id = id
        this.firstName = firstName
        this.lastName = lastName
        this.fullName = "$firstName $lastName"
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        firstName = parcel.readString()
        lastName = parcel.readString()
        fullName = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id.toString())
        dest?.writeString(firstName)
        dest?.writeString(lastName)
        dest?.writeString(fullName)
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

    override fun toString(): String {
        return this.fullName!!
    }
}