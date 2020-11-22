package com.ap.iwasthere.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import java.util.*
import kotlin.collections.HashMap

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

    var firstName: String? = null

    var lastName: String? = null

    @get: Exclude
    var fullName: String? = null
        get() {
            return "$firstName $lastName"
        }

    var signatures: HashMap<String, Signature>? = HashMap()

    /**
     * Constructor.
     *
     * @param id the unique identifier of the student
     * @param firstName the first name of the student
     * @param lastName the last name of the student
     */
    constructor(id: String, firstName: String?, lastName: String?, signatures: HashMap<String, Signature>) : this() {
        this.id = id
        this.firstName = firstName
        this.lastName = lastName
        this.signatures = signatures
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        firstName = parcel.readString()
        lastName = parcel.readString()
        signatures = parcel.readHashMap(Signature::class.java.classLoader) as HashMap<String, Signature>?
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(firstName)
        dest?.writeString(lastName)
        dest?.writeMap(signatures)
    }

    override fun describeContents(): Int {
        return 0
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
        return fullName!!
    }

    fun formatLastName(name: String): String {
        return name.replace("[", "").replace("]", "").replace(",", "")
    }

    fun makeStudent(firstName: String, lastName: String): Student {
        return Student(
            UUID.randomUUID().toString(),
            firstName,
            lastName,
            HashMap()
        )
    }
}