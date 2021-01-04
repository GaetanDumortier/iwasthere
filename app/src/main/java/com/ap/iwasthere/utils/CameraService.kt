package com.ap.iwasthere.utils

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.hardware.Camera
import android.os.AsyncTask
import android.os.Handler
import android.os.IBinder
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException


class CameraService : Service(), SurfaceHolder.Callback {
    private var mCamera: Camera? = null

    // the camera parameters
    private var parameters: Camera.Parameters? = null
    private var bmp: Bitmap? = null
    var fo: FileOutputStream? = null
    private var FLASH_MODE: String? = null
    private var QUALITY_MODE = 0
    private var pictureSize: Camera.Size? = null
    var sv: SurfaceView? = null
    private var sHolder: SurfaceHolder? = null
    private var windowManager: WindowManager? = null
    var params: WindowManager.LayoutParams? = null
    var cameraIntent: Intent? = null
    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var width = 0
    var height = 0

    private fun openFrontFacingCameraGingerbread(): Camera? {
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.release()
        }
        var cam: Camera? = null
        val cameraInfo = Camera.CameraInfo()
        val cameraCount: Int = Camera.getNumberOfCameras()
        for (camIdx in 0 until cameraCount) {
            Camera.getCameraInfo(camIdx, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx)
                } catch (e: RuntimeException) {
                    Log.e(
                        "Camera",
                        "Camera failed to open: " + e.localizedMessage
                    )
                }
            }
        }
        return cam
    }

    private fun setBestPictureResolution() {
        // get biggest picture size
        width = pref!!.getInt("Picture_Width", 0)
        height = pref!!.getInt("Picture_height", 0)
        if (width == 0 || height == 0) {
            pictureSize = getBiggestPictureSize(parameters)
            if (pictureSize != null) parameters
                ?.setPictureSize(pictureSize!!.width, pictureSize!!.height)
            // save width and height in sharedprefrences
            width = pictureSize!!.width
            height = pictureSize!!.height
            editor!!.putInt("Picture_Width", width)
            editor!!.putInt("Picture_height", height)
            editor!!.commit()
        } else {
            parameters!!.setPictureSize(width, height)
        }
    }

    private fun getBiggestPictureSize(parameters: Camera.Parameters?): Camera.Size? {
        var result: Camera.Size? = null
        for (size in parameters!!.supportedPictureSizes) {
            if (result == null) {
                result = size
            } else {
                val resultArea: Int = result.width * result.height
                val newArea: Int = size.width * size.height
                if (newArea > resultArea) {
                    result = size
                }
            }
        }
        return result
    }

    /** Check if this device has a camera  */
    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(
            PackageManager.FEATURE_CAMERA
        )
    }

    /** Check if this device has front camera  */
    private fun checkFrontCamera(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(
            PackageManager.FEATURE_CAMERA_FRONT
        )
    }

    var handler: Handler = Handler()

    @Synchronized
    private fun takeImage(intent: Intent) {
        if (checkCameraHardware(applicationContext)) {
            val extras = intent.extras
            if (extras != null) {
                val flashMode = extras.getString("FLASH")
                FLASH_MODE = flashMode
            }
            // set flash 0ff
            FLASH_MODE = "off"
            // only for gingerbread and newer versions
            mCamera = openFrontFacingCameraGingerbread()
            if (mCamera != null) {
                try {
                    mCamera!!.setPreviewDisplay(sv!!.holder)
                } catch (e: IOException) {
                    handler.post {
                        Toast.makeText(
                            applicationContext,
                            "API doesn't support front camera",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    stopSelf()
                }
                val parameters: Camera.Parameters = mCamera!!.parameters
                pictureSize = getBiggestPictureSize(parameters)
                if (pictureSize != null) parameters
                    .setPictureSize(pictureSize!!.width, pictureSize!!.height)

                // set camera parameters
                mCamera!!.parameters = parameters
                mCamera!!.startPreview()
                mCamera!!.takePicture(null, null, mCall)
            } else {
                mCamera = openFrontFacingCameraGingerbread()
                mCamera = null
                handler.post {
                    Toast.makeText(
                        applicationContext,
                        "Geen front-facing camera gevonden!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                stopSelf()
            }
        } else {
            handler.post {
                Toast.makeText(
                    applicationContext,
                    "Dit apparaat heeft geen camera!",
                    Toast.LENGTH_LONG
                ).show()
            }
            stopSelf()
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        cameraIntent = intent
        Log.d("ImageTakin", "StartCommand()")
        pref = applicationContext.getSharedPreferences("MyPref", 0)
        editor = pref!!.edit()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager?
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params!!.gravity = Gravity.TOP or Gravity.LEFT
        params!!.width = 1
        params!!.height = 1
        params!!.x = 0
        params!!.y = 0
        sv = SurfaceView(applicationContext)
        windowManager!!.addView(sv, params)
        sHolder = sv!!.holder
        sHolder!!.addCallback(this)

        return START_STICKY
    }

    var mCall: Camera.PictureCallback =
        Camera.PictureCallback { data, _ -> // decode the data obtained by the camera into a Bitmap
            Log.d("ImageTakin", "Done")
            if (bmp != null) bmp!!.recycle()
            System.gc()
            bmp = decodeBitmap(data)
            val bytes = ByteArrayOutputStream()
            if (bmp != null && QUALITY_MODE == 0) bmp!!.compress(
                Bitmap.CompressFormat.JPEG,
                70,
                bytes
            ) else if (bmp != null && QUALITY_MODE != 0) bmp!!.compress(Bitmap.CompressFormat.JPEG, QUALITY_MODE, bytes)

            val byteArray = bytes.toByteArray()
            val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT and Base64.NO_WRAP)
            Log.d("Camera", "Image: $encodedImage")

            if (mCamera != null) {
                mCamera!!.stopPreview()
                mCamera!!.release()
                mCamera = null
            }
            Log.d("Camera", "Image Taken !")
            if (bmp != null) {
                bmp!!.recycle()
                bmp = null
                System.gc()
            }
            mCamera = null
            handler.post {
                Toast.makeText(
                    applicationContext,
                    "Your Picture has been taken !", Toast.LENGTH_SHORT
                ).show()
            }
            stopSelf()
        }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }
        if (sv != null) windowManager!!.removeView(sv)
        super.onDestroy()
    }

    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int,
        height: Int
    ) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (cameraIntent != null) TakeImage(this).execute(cameraIntent)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }
    }

    companion object {
        // Camera is not available (in use or does not exist)
        // returns null if camera is unavailable
        // attempt to get a Camera instance
        val cameraInstance: Camera?
            get() {
                var c: Camera? = null
                try {
                    c = Camera.open() // attempt to get a Camera instance
                } catch (e: Exception) {
                    // Camera is not available (in use or does not exist)
                }
                return c // returns null if camera is unavailable
            }

        fun decodeBitmap(data: ByteArray?): Bitmap? {
            var bitmap: Bitmap? = null
            val bfOptions = BitmapFactory.Options()
            bfOptions.inDither = false // Disable Dithering mode
            bfOptions.inPurgeable = true // Tell to gc that whether it needs free
            // memory, the Bitmap can be cleared
            bfOptions.inInputShareable = true // Which kind of reference will be
            // used to recover the Bitmap data
            // after being clear, when it will
            // be used in the future
            bfOptions.inTempStorage = ByteArray(32 * 1024)
            if (data != null) bitmap = BitmapFactory.decodeByteArray(
                data, 0, data.size,
                bfOptions
            )
            return bitmap
        }

        private class TakeImage(private val cameraService: CameraService) : AsyncTask<Intent?, Void?, Void?>() {
            override fun onPostExecute(result: Void?) {}
            override fun doInBackground(vararg params: Intent?): Void? {
                cameraService.takeImage(params[0]!!)
                return null
            }

        }
    }
}