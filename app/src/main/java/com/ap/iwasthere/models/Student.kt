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

    var fullName: String? = null
        get() = field
        // We can pre-define the fields here. Will never really change
        set(value) {
            field = """$firstName $lastName"""
        }

    var signatures: ArrayList<Signature> = ArrayList()
        get() = field
        set(value) {
            field = value
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
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        firstName = parcel.readString()
        lastName = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(firstName)
        dest?.writeString(lastName)
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

    fun setFullName() {
        this.fullName = "$firstName $lastName"
    }

    override fun toString(): String {
        if (this.fullName == null) {
            this.setFullName()
        }

        return this.fullName!!
    }

    fun formatLastName(name: String): String {
        return name.replace("[", "").replace("]", "").replace(",", "")
    }

    fun makeStudent(firstName: String, lastName: String): Student {
        val student = Student(UUID.randomUUID().toString(), firstName, lastName)
        student.setFullName()

        return student
    }
}