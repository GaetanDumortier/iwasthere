package com.ap.iwasthere.helpers

import com.ap.iwasthere.models.Student
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseHelper {
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val students: ArrayList<Student> = ArrayList()
    private var ds: DataSnapshot? = null

    /*
    fun getAllStudents() {
        val usersRef = rootRef.child("students")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ds = dataSnapshot
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val student = ds.getValue(Student::class.java)
                        students.add(student!!)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        usersRef.addListenerForSingleValueEvent(eventListener)
    }
     */

    /**
     * Add a new provided student to the database.
     *
     * @param student the student object to add
     */
    fun addStudent(student: Student) {
        rootRef.child("students").child(student.id.toString()).setValue(student)
    }

}