package com.ap.iwasthere.helpers

import android.content.Context
import android.graphics.*
import android.os.Environment
import android.util.Base64
import com.ap.iwasthere.models.CanvasView
import com.ap.iwasthere.models.Student
import java.io.*
import java.lang.StringBuilder
import java.util.*

/**
 * Getting the content of a Canvas and converting it to a Bitmap was a serious pain in the ass.
 * Many thanks to this guide:
 * http://www.codeplayon.com/2019/05/android-canvas-tutorial-capture-digital-signature-and-paintview-and-save/
 *
 * @author Gaetan Dumortier
 * @since 13 November 2020
 */
class SignatureHelper(private val context: Context, private val canvasView: CanvasView) {

    /**
     * Save a signature from the provided CanvasView to the local storage of the device.
     * It takes the full name of the student to include in the filename.
     *
     * @param student the student Object, required to format the filename
     * @return True if saving of file was successful. False on failure
     */
    fun saveSignature(student: Student): Boolean {
        val storage: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val folder = File(storage.absolutePath + "/signatures")
        var success = false

        if (!folder.exists()) {
            try {
                success = folder.mkdirs()
            } catch (e: IOException) {
                println("Error creating directory: " + e.message)
            }
        }

        val file = File(folder, formatFileName(student))
        if (!file.exists()) {
            success = file.createNewFile()
        }

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
            bitMapToString(save)

            ostream.flush()
            ostream.close()
            success = true
        } catch (e: NullPointerException) {
            println("Error writing to file: " + e.message)
        } catch (e: FileNotFoundException) {
            println("Error with file. Not found?: " + e.message)
        }

        return success
    }

    private fun bitMapToString(userImage1: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        val b: ByteArray = baos.toByteArray()

        userImage1.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    /**
     * Get the current date + time and append to filename.
     * This way, we can store multiple signature files without having to overwrite the previous one.
     * This can be useful to compare signatures of a student.
     */
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

}