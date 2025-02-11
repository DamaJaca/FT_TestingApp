package com.djc.ft_testingapp.domain

import android.graphics.Bitmap
import com.djc.ft_testingapp.data.databse.entities.FaceEntity
import com.djc.ft_testingapp.domain.model.FaceModel
import com.djc.ft_testingapp.domain.model.UserModel


interface Repository {

    suspend fun getAllFaces () : List<FaceModel>?

    suspend fun saveAllFaces (list : List <FaceModel>)

    suspend fun saveFace(embed : FloatArray)

    suspend fun getUser (faceId:String): UserModel

    suspend fun clearAll ()

    fun saveUser(user:UserModel)
}