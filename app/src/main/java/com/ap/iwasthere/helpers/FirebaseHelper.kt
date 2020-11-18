package com.ap.iwasthere.helpers

import android.util.Log
import com.ap.iwasthere.BuildConfig
import com.ap.iwasthere.models.*
import com.google.firebase.database.*

/**
 * A helper class which is contains actions and properties which are required to perform
 * actions on the Firebase database.
 *
 * @author Gaetan Dumortier
 * @since 13 November 2020
 */
class FirebaseHelper {
    private val TAG = "FirebaseHelper"
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val studentsChild = "students"
    private val signaturesChild = "signatures"
    private val studentsRef = rootRef.child(studentsChild)
    private val signaturesRef = rootRef.child(signaturesChild)

    /**
     * Add a new provided student to the database.
     *
     * @param student the student object to add
     * @param itemCallback the callback to be executed once data is received
     */
    fun addStudent(student: Student, itemCallback: FirebaseCallback.ItemCallback?) {
        rootRef.child(studentsChild).child(student.id!!).setValue(student).addOnCompleteListener {
            itemCallback?.onItemCallback(student)
        }
    }

    /**
     * Add a new provided signature for a given user to the database.
     *
     * @param studentId the unique identifier of the user to add the signature for
     * @param signature the Signature object to add
     */
    fun addSignature(studentId: String, signature: Signature, itemCallback: FirebaseCallback.ItemCallback?) {
        signaturesRef.child(studentId).setValue(signature).addOnCompleteListener {
            itemCallback?.onItemCallback(signature)
        }

        // signaturesRef.child(studentId).setValue(signature)
        // rootRef.child(signaturesChild).child(studentId).setValue(signature)
    }

    /**
     * Fetch all students from the database.
     *
     * @param listCallback the callback to be executed once data is received
     */
    fun fetchAllStudents(listCallback: FirebaseCallback.ListCallback?) {
        studentsRef.addValueEventListener(
            studentsRef.orderByKey().addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val studentsList = ArrayList<Student>()
                        for (ds in snapshot.children) {
                            val student = ds.getValue(Student::class.java)
                            student!!.setFullName()
                            studentsList.add(student)
                        }
                        listCallback?.onListCallback(studentsList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Error executing fetchAllStudentsQuery. onCancelled thrown: " + error.message)
                    listCallback?.onListCallback(emptyList<Student>())
                }
            })
        )
    }

    /**
     * Fetch a specific student from the database.
     *
     * @param userid the unique identifier of the student to retrieve
     * @param itemCallback the callback to be executed once data is received
     */
    fun fetchStudentById(userid: String, itemCallback: FirebaseCallback.ItemCallback?) {
        studentsRef.equalTo(userid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val student: Student? = null
                    Log.d(TAG, "snapshot: $snapshot")
                    //itemCallback?.onStudentCallback(student!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Error executing fetchStudentById: " + error.message)
                itemCallback?.onItemCallback(Student())
            }
        })
    }

    /**
     * Fetch all signatures from a provided student from the database.
     *
     * @param userId the unique identifier of the student to retrieve the signatures for
     * @param listCallback the callback to be executed once data is received
     */
    fun fetchAllSignaturesFromUser(userId: String, listCallback: FirebaseCallback.ListCallback?) {
        signaturesRef.addValueEventListener(
            signaturesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (ds in snapshot.children) {
                            Log.d(TAG, "signatures DS: $ds")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Error executing fetchAllStudentsQuery. onCancelled thrown: " + error.message)
                    listCallback?.onListCallback(emptyList())
                }
            })
        )
    }

    /**
     * Fetch the admin password from the database. If no password can be found
     * the callback defined in gradle.properties will be used.
     *
     * @param itemCallback the callback to be executed once data is received
     */
    fun getAdminPassword(itemCallback: FirebaseCallback.ItemCallback?) {
        rootRef.addValueEventListener(
            rootRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var adminPassword = ""
                    if (snapshot.exists()) {
                        if (snapshot.hasChild("admin_password")) {
                            adminPassword = snapshot.child("admin_password").value.toString()
                        }
                    }
                    itemCallback?.onItemCallback(adminPassword)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Error executing getAdminPassword. Fallback password will be used. " + error.message)
                    itemCallback?.onItemCallback(BuildConfig.ADMIN_PASSWORD)
                }
            })
        )
    }
}