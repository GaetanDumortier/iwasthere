package com.ap.iwasthere.helpers

import com.ap.iwasthere.models.Student
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {
    private val rootRef = FirebaseDatabase.getInstance().reference

    /**
     * Add a new provided student to the database.
     *
     * @param student the student object to add
     */
    fun addStudent(student: Student) {
        rootRef.child("students").child(student.id.toString()).setValue(student)
    }

}