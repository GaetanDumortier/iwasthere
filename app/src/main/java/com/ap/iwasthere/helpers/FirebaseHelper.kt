package com.ap.iwasthere.helpers

import android.util.Log
import com.ap.iwasthere.models.FirebaseCallBack
import com.ap.iwasthere.models.Signature
import com.ap.iwasthere.models.Student
import com.google.firebase.database.*

/**
 * A helper class which is contains actions and properties which are required to perform
 * actions on the Firebase database.
 *
 * @author Gaetan Dumortier
 * @since 13 November 2020
 */
class FirebaseHelper {
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val studentsChild = "students"
    private val signaturesChild = "signatures"
    private val studentsRef = rootRef.child(studentsChild)
    private val signaturesRef = rootRef.child(signaturesChild)

    /**
     * Add a new provided student to the database.
     *
     * @param student the student object to add
     */
    fun addStudent(student: Student) {
        rootRef.child(studentsChild).child(student.id!!).setValue(student)
    }

    /**
     * Add a new provided signature for a given user to the database.
     *
     * @param studentId the unique identifier of the user to add the signature for
     * @param signature the Signature object to add
     */
    fun addSignature(studentId: String, signature: Signature) {
        rootRef
            .child(signaturesChild)
            .child(studentId)
            .setValue(signature)
    }

    /**
     * Fetch all students from the database and invoke the
     */
    fun fetchAllStudents(firebaseCallBack: FirebaseCallBack) {
        studentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val studentsList = ArrayList<Student>()
                    for (ds in snapshot.children) {
                        val student = ds.getValue(Student::class.java)
                        student!!.setFullName()
                        studentsList.add(student)
                    }
                    firebaseCallBack.onStudentCallBack(studentsList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseHelper", "Error executing fetchAllStudentsQuery. onCancelled thrown: " + error.message)
                firebaseCallBack.onStudentCallBack(emptyList())
            }
        })
    }

    fun fetchAllSignaturesFromUser(userId: String, firebaseCallBack: FirebaseCallBack) {
        signaturesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        Log.d("FirebaseHelper", "signatures DS: $ds")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseHelper", "Error executing fetchAllStudentsQuery. onCancelled thrown: " + error.message)
                firebaseCallBack.onStudentCallBack(emptyList())
            }
        })
    }
}