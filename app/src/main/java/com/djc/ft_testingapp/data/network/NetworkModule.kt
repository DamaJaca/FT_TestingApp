package com.djc.ft_testingapp.data.network

import android.app.Application
import android.content.Context
import com.djc.ft_testingapp.data.RepositoryImpl
import com.djc.ft_testingapp.data.databse.FacesDataBase
import com.djc.ft_testingapp.domain.Repository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesRepository (firebaseStore: FirebaseFirestore, dataBase: FacesDataBase): Repository {
        return RepositoryImpl (firebaseStore, dataBase)
    }

    @Provides
    @Singleton
    fun providesFirebaseStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }


    @Provides
    @Singleton
    fun providesModel(@ApplicationContext context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    @Provides
    @Singleton
    fun providesInterpreter(@ApplicationContext context: Context, model :MappedByteBuffer): Interpreter {
        return Interpreter(model, Interpreter.Options().setUseXNNPACK(true))
    }



}