package com.djc.ft_testingapp.ui.facerecord

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalMirrorMode
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.MirrorMode
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.djc.ft_testingapp.databinding.FragmentFaceRecordBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FaceRecord : Fragment() {


    private var _binding: FragmentFaceRecordBinding? = null
    private val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null

    private var croppedFaceBitmap: Bitmap? = null

    private lateinit var faceDetector : FaceDetector

    private val viewModel by activityViewModels<FaceRecordViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initListeners()
    }



    private fun initListeners() {
        binding.fbAddFace.setOnClickListener {
            takePhotoAndCropFace()

        }
    }

    private fun initUI() {
        checkCameraPermission()
        initListeners()

    }

    private fun initFaceDetector() {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()
        faceDetector = FaceDetection.getClient(options)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                initCamera()
            } else {
                // Permiso denegado, muestra un mensaje o maneja el caso
                Toast.makeText(requireContext(), "Se necesita permiso para usar la cámara", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // El permiso no ha sido concedido, solicitamos el permiso
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1001)
        } else {
            // El permiso ya fue concedido, puedes iniciar la cámara
            initCamera()
        }
    }

    @OptIn(ExperimentalMirrorMode::class)
    private fun initCamera() {
        initFaceDetector()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Configuramos el Preview
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }

            // Configuramos ImageCapture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Configuramos ImageAnalysis para reconocimiento facial en tiempo real
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }


            // Seleccionamos la cámara (por ejemplo, la frontal)
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Desvinculamos casos de uso anteriores
                cameraProvider.unbindAll()
                // Vinculamos los casos de uso al ciclo de vida
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("CameraFragment", "Error al iniciar la cámara", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            faceDetector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        if (faces.isNotEmpty()) {
                            val face = faces[0]
                            val bounds = face.boundingBox
                            val viewWidth = binding.overlayView.width.toFloat()
                            val viewHeight = binding.overlayView.height.toFloat()

                            // Obtener dimensiones de la imagen, teniendo en cuenta la rotación
                            val rotation = imageProxy.imageInfo.rotationDegrees
                            val imageWidth: Float
                            val imageHeight: Float
                            if (rotation == 90 || rotation == 270) {
                                imageWidth = imageProxy.height.toFloat()
                                imageHeight = imageProxy.width.toFloat()
                            } else {
                                imageWidth = imageProxy.width.toFloat()
                                imageHeight = imageProxy.height.toFloat()
                            }

                            // Para un escalado fit (toda la imagen se muestra), usamos el mínimo de los factores de escala.
                            val scaleX = viewWidth / imageWidth
                            val scaleY = viewHeight / imageHeight
                            val scale = minOf(scaleX, scaleY)

                            // Calcular las dimensiones escaladas de la imagen
                            val scaledImageWidth = imageWidth * scale
                            val scaledImageHeight = imageHeight * scale

                            // Calcular los offsets (las barras horizontales o verticales que se añaden para centrar la imagen)
                            val offsetX = (viewWidth - scaledImageWidth) / -2f
                            val offsetY = (viewHeight - scaledImageHeight) / 2f

                            // Transformar el bounding box sumándole el offset y aplicando el escalado
                            val finalRect = Rect(
                                (bounds.left * scale + offsetX).toInt(),
                                (bounds.top * scale + offsetY).toInt(),
                                (bounds.right * scale + offsetX).toInt(),
                                (bounds.bottom * scale + offsetY).toInt()
                            )
                            binding.overlayView.setFaceRect(finalRect)
                        } else {
                            binding.overlayView.clear()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("CameraFragment", "Error en detección facial", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
    private fun takePhotoAndCropFace() {
        imageCapture?.takePicture(ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val bitmap = imageProxy.toBitmap()
                    imageProxy.close()

                    val inputImage = InputImage.fromBitmap(bitmap, 0)
                    faceDetector.process(inputImage)
                        .addOnSuccessListener { faces ->
                            if (faces.isNotEmpty()) {
                                val face = faces[0]
                                croppedFaceBitmap = cropBitmap(bitmap, face.boundingBox)
                                viewModel.setBitmap(croppedFaceBitmap!!)
                                findNavController().navigate(FaceRecordDirections.actionFaceRecordToRegisterFragment2())
                            } else {
                                Log.d("CameraFragment", "No se detectó ningún rostro en la imagen capturada")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("CameraFragment", "Error al procesar la imagen capturada", e)
                        }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraFragment", "Error al capturar imagen", exception)
                }
            })
    }

    private fun cropBitmap(bitmap: Bitmap, rect: Rect): Bitmap {
        val left = rect.left.coerceAtLeast(0)
        val top = rect.top.coerceAtLeast(0)
        val right = rect.right.coerceAtMost(bitmap.width)
        val bottom = rect.bottom.coerceAtMost(bitmap.height)
        return Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFaceRecordBinding.inflate(inflater, container, false)
        return binding.root
    }


}