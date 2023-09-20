package com.example.translateapp.ui.camera


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.translateapp.MainActivity
import com.example.translateapp.R
import com.example.translateapp.databinding.FragmentCameraBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraFragment : Fragment() {
    lateinit var binding: FragmentCameraBinding
    lateinit var shoot: ImageView
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    lateinit var preview: PreviewView
    var bitmap: Bitmap? = null
    lateinit var sendTranslate: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentCameraBinding.inflate(layoutInflater)
        shoot = binding.shoot
        preview = binding.preview
        sendTranslate = binding.sendTranslate

        (requireActivity() as MainActivity).findViewById<LinearLayout>(R.id.bottom_nav_bar).visibility = View.INVISIBLE

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        shoot.setOnClickListener {
            takePhoto()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        sendTranslate.setOnClickListener {
            if (bitmap != null) {
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(InputImage.fromBitmap(bitmap!!, 0))
                    .addOnSuccessListener {
                        findNavController().navigate(CameraFragmentDirections
                            .actionCameraFragmentToMainFragment(it.text))
                    }
            }
        }

        return binding.root
    }

    private fun takePhoto() {
        // Get a stable reference of the
        // modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image

        // Set up image capture listener,
        // which is triggered after photo has
        // been taken

        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()),
            object : OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    binding.shoot.visibility = View.INVISIBLE
                    binding.gallery.visibility = View.INVISIBLE
                    binding.flash.visibility = View.INVISIBLE
                    binding.image.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(rotateImage(image.toBitmap(), 90f)).into(binding.image)
                    preview.visibility = View.INVISIBLE
                    binding.sendTranslate.visibility = View.VISIBLE
                    bitmap = rotateImage(image.toBitmap(), 90f)

                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    val errorType = exception.imageCaptureError
                    Log.e("hello", errorType.toString())
                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(preview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    // checks the camera permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // If all permissions granted , then start Camera
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                // If permissions are not granted,
                // present a toast to notify the user that
                // the permissions were not granted.
                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXGFG"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

//    private fun detectTxt(imageBitmap: Bitmap) {
//        // this is a method to detect a text from image.
//        // below line is to create variable for firebase
//        // vision image and we are getting image bitmap.
//        val image = FirebaseVisionImage.fromBitmap(imageBitmap)
//
//        // below line is to create a variable for detector and we
//        // are getting vision text detector from our firebase vision.
//        val detector: FirebaseVisionTextDetector =
//            FirebaseVision.getInstance().visionTextDetector
//
//        // adding on success listener method to detect the text from image.
//        detector.detectInImage(image)
//            .addOnSuccessListener(OnSuccessListener<FirebaseVisionText?> { firebaseVisionText -> // calling a method to process
//                // our text after extracting.
//                processTxt(firebaseVisionText)
//            }).addOnFailureListener(OnFailureListener { // handling an error listener.
//                Toast.makeText(
//                    requireContext(),
//                    "Fail to detect the text from image..",
//                    Toast.LENGTH_SHORT
//                ).show()
//            })
//    }
//
//    private fun processTxt(text: FirebaseVisionText) {
//        // below line is to create a list of vision blocks which
//        // we will get from our firebase vision text.
//        val blocks: List<FirebaseVisionText.Block> = text.blocks
//
//        // checking if the size of the
//        // block is not equal to zero.
//        if (blocks.isEmpty()) {
//            // if the size of blocks is zero then we are displaying
//            // a toast message as no text detected.
//            Toast.makeText(requireContext(), "No Text ", Toast.LENGTH_LONG).show()
//            return
//        }
//        // extracting data from each block using a for loop.
//        for (block in text.blocks) {
//            // below line is to get text
//            // from each block.
//            val txt: String = block.text
//
//            // below line is to set our
//            // string to our text view.
//            //findNavController().navigate(CameraFragmentDirections.actionCameraFragmentToHomeFragment(txt))
//            Log.e("hello", txt)
//        }
//    }
}