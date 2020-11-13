package com.ap.iwasthere.helpers

import android.content.Context
import android.graphics.*
import android.os.Environment
import android.util.Base64
import android.widget.Toast
import com.ap.iwasthere.models.CanvasView
import java.io.*

/**
 * Getting the content of a Canvas and converting it to a Bitmap was a serious pain in the ass.
 * Many thanks to this guide:
 * http://www.codeplayon.com/2019/05/android-canvas-tutorial-capture-digital-signature-and-paintview-and-save/
 *
 * @author Gaetan Dumortier
 * @since 13 November 2020
 */
class SignatureHelper(private val context: Context, private val canvasView: CanvasView) {

    fun saveSignature(studentName: String): Boolean {
        val storage: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val folder = File(storage.absolutePath + "/signatures")
        var success = false

        if (!folder.exists()) {
            success = folder.mkdirs()
        }

        val name = studentName.replace("\\s".toRegex(), "_")
        val file = File(folder, String.format("/%s_signature.jpg", name))

        if (!file.exists()) {
            try {
                success = file.createNewFile()
            } catch (e: IOException) {
                println("Error writing signature image: " + e.message)
            }
        }

        try {
            val ostream = FileOutputStream(file)
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
        } catch (e: NullPointerException) {
            e.printStackTrace()
            Toast.makeText(
                context, "Null error",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(context, "File error", Toast.LENGTH_SHORT).show()
        }

        return success
    }

    private fun bitMapToString(userImage1: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        val b: ByteArray = baos.toByteArray()

        userImage1.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

}