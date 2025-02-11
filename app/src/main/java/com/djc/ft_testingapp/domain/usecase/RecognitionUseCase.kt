package com.djc.ft_testingapp.domain.usecase

import android.graphics.Bitmap
import com.djc.ft_testingapp.domain.Repository
import com.djc.ft_testingapp.domain.model.FaceModel
import com.djc.ft_testingapp.domain.model.UserModel
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import kotlin.math.sqrt

class RecognitionUseCase @Inject constructor (
    private val repository: Repository,
    private val interpreter: Interpreter
) {

    //Esta función hace que devuelva el ID que se aproxime más al bitmap que le pasa, en caso de no encontrar ninguno, devuelve Null
    suspend operator fun invoke(bitmap: Bitmap): UserModel {
        val facesList = repository.getAllFaces() ?: emptyList()
        var idUser :String? =""


            idUser= identificarRostro(getFaceEmbedding(bitmap), facesList)
            if (idUser!=null){
                return repository.getUser(idUser)
            }
            else{
                kotlin.runCatching { repository.saveFace(getFaceEmbedding(bitmap)) }
                idUser = identificarRostro(getFaceEmbedding(bitmap), facesList)
            }

        return UserModel(idUser!!, "","", "")
    }


    //Funciónes complementarias
    fun convertToByteBuffer(bitmap: Bitmap, inputSize: Int): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val intValues = IntArray(inputSize * inputSize)
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)

        // Convierte cada pixel en float (ejemplo: normalizar a [0,1])
        var pixelIndex = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixelValue = intValues[pixelIndex++]
                // Extraer los canales RGB
                val r = ((pixelValue shr 16) and 0xFF) / 255.0f
                val g = ((pixelValue shr 8) and 0xFF) / 255.0f
                val b = (pixelValue and 0xFF) / 255.0f

                byteBuffer.putFloat(r)
                byteBuffer.putFloat(g)
                byteBuffer.putFloat(b)
            }
        }
        return byteBuffer
    }

    suspend fun getFaceEmbedding(
        bitmap: Bitmap,
        inputSize: Int = 160,
        embeddingDim: Int = 128
    ): FloatArray = withContext(Dispatchers.Default) {
        // Preprocesar la imagen y obtener el ByteBuffer
        val inputBuffer = convertToByteBuffer(bitmap, inputSize)

        // Crear la estructura de salida del modelo
        val outputArray = Array(1) { FloatArray(embeddingDim) }

        // Ejecutar la inferencia
        interpreter.run(inputBuffer, outputArray)

        // Retorna el vector obtenido (embedding)
        outputArray[0]
    }

    private fun identificarRostro(newEmbedding: FloatArray, list : List <FaceModel>): String? {
        var mejorId: String? = null
        var mejorSimilitud = -1f
        for ((id, embedding) in list) {
            val similitud = cosineSimilarity(newEmbedding, embedding)
            if (similitud > mejorSimilitud) {
                mejorSimilitud = similitud
                mejorId = id.toString()
            }
        }
        return if (mejorSimilitud >= 0.4f) mejorId else null
    }

    private fun cosineSimilarity(emb1: FloatArray, emb2: FloatArray): Float {
        var dotProduct = 0f
        var normA = 0f
        var normB = 0f
        for (i in emb1.indices) {
            dotProduct += emb1[i] * emb2[i]
            normA += emb1[i] * emb1[i]
            normB += emb2[i] * emb2[i]
        }
        normA = sqrt(normA)
        normB = sqrt(normB)
        return dotProduct / (normA * normB)
    }

}