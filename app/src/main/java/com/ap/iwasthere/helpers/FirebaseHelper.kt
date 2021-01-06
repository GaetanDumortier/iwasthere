package com.ap.iwasthere.helpers

import android.util.Log
import com.ap.iwasthere.BuildConfig
import com.ap.iwasthere.models.FirebaseCallback
import com.ap.iwasthere.models.Signature
import com.ap.iwasthere.models.Student
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    private var rootRef = FirebaseDatabase.getInstance().reference
    private val studentsChild = "students"
    private val signaturesChild = "signatures"
    private val studentsRef = rootRef.child(studentsChild)

    /**
     * Wipe all entries under the database students node (including signatures)
     * Not gonna handle any callbacks here, just delete immediately without error-checking etc.
     */
    fun wipeDatabase() {
        studentsRef.removeValue()
    }

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
        studentsRef.child(signature.studentId!!).child(signaturesChild).child(signature.id!!).setValue(signature)
            .addOnCompleteListener {
                itemCallback?.onItemCallback(signature)
            }
    }

    /**
     * Remove a student with provided identifier from the database
     * This will also remove all the signatures of this user
     *
     * @param studentId the unique identifier of the student
     */
    fun deleteStudent(studentId: String, itemCallback: FirebaseCallback.ItemCallback?) {
        studentsRef.child(studentId).removeValue()
            .addOnSuccessListener {
                itemCallback?.onItemCallback(true)
            }
            .addOnFailureListener {
                itemCallback?.onItemCallback(false)
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
     * Verify whether a student with the provided identifier already exists or not
     *
     * @param studentId the unique identifier of the student to verify
     */
    fun studentExists(studentId: String, itemCallback: FirebaseCallback.ItemCallback?) {
        studentsRef.orderByChild("number").equalTo(studentId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    itemCallback?.onItemCallback(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Error executing studentExists. onCancelled thrown: " + error.message)
                    itemCallback?.onItemCallback(false)
                }
            })
    }

    /**
     * Get the student's name by provided identifier
     *
     * @param studentId the unique identifier of the student
     */
    fun fetchStudentNameById(studentId: String, itemCallback: FirebaseCallback.ItemCallback?) {
        studentsRef.child(studentId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var studentName: String? = null
                    var firstName: String? = null
                    for (ds in snapshot.children) {
                        if (ds.key.equals("firstName")) {
                            firstName = ds.getValue(String::class.java)
                        }
                        if (ds.key.equals("lastName")) {
                            studentName = "$firstName ${ds.getValue(String::class.java)}"
                        }
                    }
                    itemCallback?.onItemCallback(studentName!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Error executing fetchStudentById: ${error.message}")
            }
        })
    }

    /**
     * Fetch all signatures from a provided student from the database.
     *
     * @param student the student to retrieve the signatures for
     * @param listCallback the callback to be executed once data is received
     * @param limit the max amount of signatures to return (def. 0, disabled)
     */
    fun fetchAllSignaturesFromUser(student: Student, listCallback: FirebaseCallback.ListCallback?, limit: Int = 0) {
        var queryStr = studentsRef
            .child(student.id!!)
            .child(signaturesChild)
            .orderByValue()
        if (limit > 0) {
            queryStr = studentsRef
                .child(student.id!!)
                .child(signaturesChild)
                .orderByValue()
                .limitToFirst(limit)
        }

        queryStr.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val signatures: ArrayList<Signature> = ArrayList()
                    for (ds in snapshot.children) {
                        val signature = ds.getValue(Signature::class.java)
                        signatures.add(signature!!)
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
            var adminPassword = BuildConfig.ADMIN_PASSWORD
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChild("admin_password")) {
                    adminPassword = snapshot.child("admin_password").value.toString()
                }
                itemCallback?.onItemCallback(adminPassword)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Error executing getAdminPassword. Fallback password will be used. " + error.message)
                itemCallback?.onItemCallback(adminPassword)
            }
        })
    }

    /**
     * Check whether facial detection is enabled in the database or not.
     *
     * @param itemCallback the callback to be executed once data is received
     */
    fun isFaceDetectionEnabled(itemCallback: FirebaseCallback.ItemCallback?) {
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            var facialDetection = false // disable by default I guess
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChild("facedetection")) {
                    facialDetection = snapshot.child("facedetection").value.toString().toBoolean()
                }
                itemCallback?.onItemCallback(facialDetection)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Error executing isFaceDetectionEnabled. Defaulting to true. " + error.message)
                itemCallback?.onItemCallback(facialDetection)
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

    /**
     * Enable or disable the face detection
     *
     * @param value the new value to be set (true|false)
     */
    suspend fun setFaceDetection(value: Boolean) {
        rootRef.child("facedetection").setValue(value).await()
    }
}