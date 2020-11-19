package com.ap.iwasthere.helpers

import android.util.Log
import com.ap.iwasthere.BuildConfig
import com.ap.iwasthere.models.FirebaseCallback
import com.ap.iwasthere.models.Signature
import com.ap.iwasthere.models.Student
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await


/**
 * A helper class which is contains actions and properties which are required to perform
 * actions on the Firebase database.
 *
 * @author Gaetan Dumortier
 * @since 13 November 2020
 */
class FirebaseHelper {
    private val TAG = "FirebaseHelper"
    val DefaultAdminPassword = "iwasnotthere"

    private var rootRef = FirebaseDatabase.getInstance().reference
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
     * @param signature the Signature object to add
     */
    fun addSignature(signature: Signature, itemCallback: FirebaseCallback.ItemCallback?) {
        signaturesRef.child(signature.id!!).setValue(signature).addOnCompleteListener {
            itemCallback?.onItemCallback(signature)
        }
    }

    /**
     * Fetch all students from the database.
     *
     * @param listCallback the callback to be executed once data is received
     */
    fun fetchAllStudents(listCallback: FirebaseCallback.ListCallback?) {
        studentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val studentsList = ArrayList<Student>()
                    for (ds in snapshot.children) {
                        val student = ds.getValue(Student::class.java)
                        studentsList.add(student!!)
                    }
                    listCallback?.onListCallback(studentsList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Error executing fetchAllStudentsQuery. onCancelled thrown: " + error.message)
                listCallback?.onListCallback(emptyList<Student>())
            }
        })
    }

    /**
     * Fetch a specific student from the database.
     *
     * @param studentId the unique identifier of the student to retrieve
     * @param itemCallback the callback to be executed once data is received
     */
    fun fetchStudentById(studentId: String, itemCallback: FirebaseCallback.ItemCallback?) {
        studentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val student: Student? = snapshot.getValue(Student::class.java)
                    itemCallback?.onItemCallback(student!!)
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
     * @param studentId the unique identifier of the student to retrieve the signatures for
     * @param listCallback the callback to be executed once data is received
     * @param limit the max amount of signatures to return (def. 0, disabled)
     */
    fun fetchAllSignaturesFromUser(studentId: String, listCallback: FirebaseCallback.ListCallback?, limit: Int = 0) {
        var queryStr = signaturesRef.orderByValue()
        if (limit > 0) {
            queryStr = signaturesRef.orderByValue().limitToFirst(limit)
        }

        // TODO: add filter for studentId in querystring instead of in for-loop.
        queryStr.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val signatures: ArrayList<Signature> = ArrayList()
                    for (ds in snapshot.children) {
                        val signature = ds.getValue(Signature::class.java)
                        if (signature!!.studentId.equals(studentId)) {
                            signatures.add(signature)
                        }
                    }
                    listCallback?.onListCallback(signatures)
                } else {
                    listCallback?.onListCallback(emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Error executing fetchAllSignaturesFromUser. onCancelled thrown: " + error.message)
                listCallback?.onListCallback(emptyList())
            }
        })
    }

    /**
     * Fetch the admin password from the database. If no password can be found
     * the callback defined in gradle.properties will be used.
     *
     * @param itemCallback the callback to be executed once data is received
     */
    fun getAdminPassword(itemCallback: FirebaseCallback.ItemCallback?) {
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var adminPassword = BuildConfig.ADMIN_PASSWORD
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
    }

    /**
     * Update the password of the admin interface.
     *
     * @param password the new password to be set
     */
    suspend fun setAdminPassword(password: String) {
        rootRef.child("admin_password").setValue(password).await()
    }
}