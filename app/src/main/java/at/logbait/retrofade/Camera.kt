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
import androidx.camera.core.Camera
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
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
import android.os.Environment
import java.io.File


typealias LumaListener = (luma: Double) -> Unit

class Camera : AppCompatActivity() {
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

        // Set up the listeners for take photo and video capture buttons

        imageCaptureButton.setOnClickListener {(Log.d("AHHHHHH","HALLO"))}
        imageCaptureButton.setOnClickListener{takePhoto()}

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

    private var currentPhotoUri: Uri? = null

    private fun takePhoto() {
        Log.d("AHHHHHH", "HALLO ICH WURDE ANGERUFEN")
        // Get a stable reference of the modifiable image capture use case
        val outputDirectory = File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "MyAppImages")
        val executor = Executors.newSingleThreadExecutor()
        val photoFile = File(outputDirectory, SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY).format(System.currentTimeMillis()) + ".png")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture?.takePicture(outputFileOptions, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // Display a message or update UI when the image is saved successfully
                val msg = "Photo capture succeeded: ${photoFile.absolutePath}"
                Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: ImageCaptureException) {
                // Display an error message or update UI when the image capture fails
                val msg = "Photo capture failed: ${exception.message}"
                Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun startCamera() {
        // Get a camera provider instance
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Camera provider is now guaranteed to be available
            val cameraProvider = cameraProviderFuture.get()

            // Set up the view finder use case to display camera preview
            val preview = Preview.Builder()
                .build()
                .also{
                    val viewFinder = findViewById<androidx.camera.view.PreviewView>(R.id.viewFinder)
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }


            // Set up the capture use case to allow users to take photos
            val imageCapture = ImageCapture.Builder()
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
