package com.ap.iwasthere.helpers

import com.ap.iwasthere.models.Signature
import com.ap.iwasthere.models.Student
import com.google.firebase.database.FirebaseDatabase

/**
 * A helper class which is contains actions and properties which are required to perform
 * actions on the Firebase database.
 *
 * @author Gaetan Dumortier
 * @since 13 November 2020
 */
class FirebaseHelper {
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val studentsRef = "students"
    private val signaturesRef = "signatures"

    /**
     * Add a new provided student to the database.
     *
     * @param student the student object to add
     */
    fun addStudent(student: Student) {
        rootRef.child("students").child(student.id!!).setValue(student)
    }

    /**
     * Add a new provided signature for a given user to the database.
     *
     * @param studentId the unique identifier of the user to add the signature for
     * @param signature the Signature object to add
     */
    fun addSignature(studentId: String, signature: Signature) {
        rootRef
            .child(signaturesRef)
            .child(studentId)
            .setValue(signature)
    }

}