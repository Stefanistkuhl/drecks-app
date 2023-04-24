package at.logbait.retrofade

import android.content.pm.PackageManager
import android.Manifest.permission.CAMERA
import android.Manifest.permission_group.CAMERA
import android.content.ContentValues
import java.util.concurrent.Executor
import android.hardware.SensorPrivacyManager.Sensors.CAMERA
import android.media.MediaRecorder.VideoSource.CAMERA
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.muttaapp.R
import com.example.muttaapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Size
import androidx.camera.core.*
import androidx.camera.core.Camera
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

typealias LumaListener = (luma: Double) -> Unit

class Camera : AppCompatActivity() {
    private var currentPhotoUri: Uri? = null

    private lateinit var viewBinding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_camera)

        val imageCaptureButton = findViewById<Button>(R.id.image_capture_button)

        // Set up the listener for the take photo button
        imageCaptureButton.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (arePermissionsGranted()) {
            startCamera()
        } else {
            // If the permission has not been granted, request it from the user
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun arePermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (arePermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun takePhoto() {
        Log.d("AHHHHHH", "HALLO ICH WURDE ANGERUFEN")

        val outputDirectory = getOutputDirectory()

        val photoName = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(System.currentTimeMillis()) + ".jpg"
        val photoFile = File(outputDirectory, photoName)

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, photoName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyAppImages")
        }

        val volumeName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        } else {
            MediaStore.VOLUME_EXTERNAL
        }
        currentPhotoUri = contentResolver.insert(MediaStore.Images.Media.getContentUri(volumeName), contentValues)


        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(contentResolver, currentPhotoUri!!, contentValues).build()

        imageCapture?.takePicture(outputFileOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // Display a message or update UI when the image is saved successfully
                val msg = "Photo capture succeeded: $currentPhotoUri"
                runOnUiThread {
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                }

                val intent = Intent(this@Camera, ImageSaveActivity::class.java)
                intent.putExtra("bild", currentPhotoUri.toString())
                startActivity(intent)
            }

            override fun onError(exception: ImageCaptureException) {
                // Display an error message or update UI when the image capture fails
                Log.e("Camera", "Photo capture failed", exception)
                val msg = "Photo capture failed: ${exception.message}"
                runOnUiThread {
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                }

            }
        })

    }


    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val options = BitmapFactory.Options().apply {
            inSampleSize = 2
        }

        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        return Bitmap.createScaledBitmap(bitmap, image.width, image.height, false)
    }

    private fun saveBitmap(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
    }

    private fun getOutputDirectory(): File {
        val appPicturesDirectory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyAppImages")
        if (appPicturesDirectory?.exists() == false) {
            appPicturesDirectory.mkdirs()
        }
        return appPicturesDirectory ?: throw IOException("Unable to create output directory")
    }


    private fun startCamera() {
        // Get a camera provider instance
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Camera provider is now guaranteed to be available
            val cameraProvider = cameraProviderFuture.get()

            Log.d("Camera", "Camera devices: ${cameraProvider.availableCameraInfos}")


            // Set up the view finder use case to display camera preview
            val preview = Preview.Builder()
                .build()
                .also {
                    val viewFinder = findViewById<androidx.camera.view.PreviewView>(R.id.viewFinder)
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // Set up the capture use case to allow users to take photos
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(1080,1920))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Choose the camera by requiring a lens facing
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            // Attach use cases to the camera with the same lifecycle owner
            val camera = cameraProvider.bindToLifecycle(
                this, // LifecycleOwner
                cameraSelector,
                preview,
                imageCapture)

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
