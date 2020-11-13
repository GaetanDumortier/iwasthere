package com.ap.iwasthere.models

import android.os.Parcel
import android.os.Parcelable

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
    var id: Int = 0
    var firstName: String? = null
    var lastName: String? = null

    /**
     * Constructor.
     *
     * @param id the unique identifier of the student
     * @param firstName the first name of the student
     * @param lastName the last name of the student
     */
    constructor(id: Int, firstName: String?, lastName: String?) : this() {
        this.id = id
        this.firstName = firstName
        this.lastName = lastName
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        firstName = parcel.readString()
        lastName = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(id)
        dest?.writeString(firstName)
        dest?.writeString(lastName)
    }

    fun getFullName(): String {
        return """$firstName $lastName"""
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