package com.djc.ft_testingapp.domain.usecase

import android.util.Log
import com.djc.ft_testingapp.domain.Repository
import com.djc.ft_testingapp.domain.model.FaceModel
import javax.inject.Inject

class FaceCRUDUseCase @Inject constructor(private val repository: Repository){

    suspend fun getFaces(): List<FaceModel> {
        return repository.getAllFaces() ?: emptyList()

    }

    suspend fun saveFace (list : List<FaceModel>){
        repository.saveAllFaces(list)
    }

    suspend fun resetFaces() {
        repository.clearAll()
    }
}