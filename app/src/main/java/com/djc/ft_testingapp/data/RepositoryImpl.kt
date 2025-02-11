package com.djc.ft_testingapp.data

import android.graphics.Bitmap
import android.util.Log
import com.djc.ft_testingapp.data.databse.FacesDataBase
import com.djc.ft_testingapp.data.databse.entities.FaceEntity
import com.djc.ft_testingapp.domain.Repository
import com.djc.ft_testingapp.domain.model.FaceModel
import com.djc.ft_testingapp.domain.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    val firebaseFirestore: FirebaseFirestore,
    val facesDataBase: FacesDataBase
) :
    Repository {
    override suspend fun getAllFaces(): List<FaceModel>? {
        kotlin.runCatching {
            facesDataBase.getFacesDao()
        }.onSuccess { database -> return database.getAllFaces().map { it.toDomain() } }
            .onFailure { Log.i("ERROR-TAG", it.message.toString()) }
        return null
    }

    override suspend fun saveAllFaces(list: List<FaceModel>) {
        kotlin.runCatching {
            facesDataBase.getFacesDao()
        }.onSuccess {
            it.insertAll(list.map { it.toDatabase() })
        }.onFailure { Log.i("ERROR-TAG", it.message.toString()) }
    }

    override suspend fun saveFace(embed: FloatArray) {
        kotlin.runCatching {
            facesDataBase.getFacesDao()
        }.onSuccess {
            it.insertFace(FaceEntity(embed= embed))
        }.onFailure { Log.i("ERROR-TAG", it.message.toString()) }
    }

    override suspend fun getUser(userId: String): UserModel {
        val document = firebaseFirestore.collection("users").document(userId)
            .get().await()
        return if (document.exists()) {
            val name = document.getString("name") ?: ""
            val age = document.getString("age") ?: ""
            val sex = document.getString("sex") ?: ""

            UserModel(userId, name, age, sex)
        } else {
            UserModel(userId,"","","")
        }

    }

    override fun saveUser(user: UserModel) {
        firebaseFirestore.collection("users").document(user.id).set(
            hashMapOf(
                "name" to user.name,
                "userId" to user.id,
                "age" to user.age,
                "sex" to user.sex
            )
        )
    }

    override suspend fun clearAll() {
        kotlin.runCatching {
            facesDataBase.getFacesDao()
        }.onSuccess {
            it.deleteAll()
        }.onFailure { Log.i("ERROR-TAG", it.message.toString()) }
    }
}