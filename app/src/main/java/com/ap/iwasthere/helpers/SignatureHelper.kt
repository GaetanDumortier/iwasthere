package com.ap.iwasthere.helpers

import android.app.Activity
import android.graphics.*
import android.util.Base64
import com.ap.iwasthere.models.CanvasView
import com.ap.iwasthere.models.Signature
import com.ap.iwasthere.models.Student
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Getting the content of a Canvas and converting it to a Bitmap was a serious pain in the ass.
 * Many thanks to this guide:
 * http://www.codeplayon.com/2019/05/android-canvas-tutorial-capture-digital-signature-and-paintview-and-save/
 *
 * @author Gaetan Dumortier
 * @since 13 November 2020
 */
class SignatureHelper(private val activity: Activity, private val canvasView: CanvasView) {
    private lateinit var location: String

    /**
     * Save a signature from the provided CanvasView to the local storage of the device.
     * It takes the full name of the student to include in the filename.
     *
     * @param student the student Object, required to format the filename
     * @return True if saving of file was successful. False on failure
     */
    suspend fun saveSignature(student: Student): Boolean {
        var success = false

        this.location = LocationHelper(activity).getLocation()

        // Create a temporarily file in the cache directory which we will delete when bitmap has been generated.
        val file = File.createTempFile("signature", "jpg", activity.cacheDir)
        val ostream: FileOutputStream?

        try {
            ostream = FileOutputStream(file)
            val well: Bitmap = canvasView.getBitMap(canvasView)!!
            val save = Bitmap.createBitmap(canvasView.width, canvasView.height, Bitmap.Config.ARGB_8888)
            val paint = Paint()
            paint.color = Color.WHITE
            val now = Canvas(save)
            now.drawRect(Rect(0, 0, canvasView.width, canvasView.height), paint)
            now.drawBitmap(
                well,
                Rect(0, 0, well.width, well.height),
                Rect(0, 0, canvasView.width, canvasView.height),
                null
            )

            save.compress(Bitmap.CompressFormat.JPEG, 100, ostream)
            val encoded = bitMapToString(save)
            buildSignature(student, encoded)

            // Clean up resourced and handlers
            ostream.flush()
            ostream.close()
            file.delete()

            success = true
        } catch (e: NullPointerException) {
            println("Error writing to file: " + e.printStackTrace())
        } catch (e: FileNotFoundException) {
            println("Error with file. Not found?: " + e.message)
        }

        return success
    }

    private fun bitMapToString(resource: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        resource.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /**
     * Build a new Signature object with provided data and add to the Student object.
     * @param resourceEncoded the signature as base64 encoded string
     */
    private fun buildSignature(student: Student, resourceEncoded: String) {
        val format: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)

        val signature: Signature?
        val signatureId = UUID.randomUUID().toString()
        val date = format.format(Date())

        signature = Signature(signatureId, date, this.location, resourceEncoded, student.id!!)
        student.signatures.add(signature)

        // Add the signature to the user in the database.
        FirebaseHelper().addSignature(student.id!!, signature)
    }

    /**
     * Get the current date + time and append to filename.
     * This way, we can store multiple signature files without having to overwrite the previous one.
     * This can be useful to compare signatures of a student.
     */
    /*
    private fun formatFileName(student: Student): String {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        student.setFullName()
        val name = student.fullName!!.replace("\\s".toRegex(), "_")
        val date = String.format(
            "%s-%s-%s_%s%s_",
            year.toString(),
            month.toString(),
            day.toString(),
            hour.toString(),
            minute.toString()
        )

        // 2020-11-13_1720_GaetanDumortier.jpg
        return (StringBuilder()).append(date).append(name).append(".jpg").toString()
    }
     */

}