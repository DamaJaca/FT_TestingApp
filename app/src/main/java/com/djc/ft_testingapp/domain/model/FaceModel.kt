package com.djc.ft_testingapp.domain.model

import com.djc.ft_testingapp.data.databse.entities.FaceEntity

data class FaceModel (
    var id : Int,
    var embedding : FloatArray
){
    fun toDatabase():FaceEntity{
        return FaceEntity(id=id, embed = embedding)
    }
}