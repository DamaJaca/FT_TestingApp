package com.djc.ft_testingapp.data.databse.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.djc.ft_testingapp.data.databse.entities.FaceEntity

@Dao
interface FacesDao {

    @Query("Select * From faces_table")
    suspend fun getAllFaces():List<FaceEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facturas:List<FaceEntity>)

    @Query("Delete from faces_table")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFace(face: FaceEntity)

}