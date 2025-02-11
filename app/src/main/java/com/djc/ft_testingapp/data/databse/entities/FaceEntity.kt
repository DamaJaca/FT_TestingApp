package com.djc.ft_testingapp.data.databse.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.djc.ft_testingapp.domain.model.FaceModel

@Entity(tableName= "faces_table")
data class FaceEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id:Int=0,
    @ColumnInfo("embedding") val embed:FloatArray
)
{
    fun toDomain(): FaceModel {
        return FaceModel(id,embed)
    }
}