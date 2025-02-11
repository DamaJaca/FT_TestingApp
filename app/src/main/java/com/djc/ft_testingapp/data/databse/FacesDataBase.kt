package com.djc.ft_testingapp.data.databse

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.djc.ft_testingapp.data.databse.dao.FacesDao
import com.djc.ft_testingapp.data.databse.entities.FaceEntity

@Database(entities = [FaceEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class FacesDataBase : RoomDatabase(){

    abstract fun getFacesDao(): FacesDao

}