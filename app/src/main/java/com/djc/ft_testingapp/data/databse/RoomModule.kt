package com.djc.ft_testingapp.data.databse

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val FACTURAS_DATABASE_NAME ="faces_database"

    @Singleton
    @Provides
    fun provideRoom (@ApplicationContext context: Context)= Room.databaseBuilder(context, FacesDataBase::class.java, FACTURAS_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideFacturaDao (db: FacesDataBase) = db.getFacesDao()
}