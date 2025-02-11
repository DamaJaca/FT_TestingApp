package com.djc.ft_testingapp.ui.facerecord

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FaceRecordViewModel : ViewModel() {


    private val _face = MutableLiveData<Bitmap?>()
    val face: LiveData<Bitmap?> get() = _face

    fun setBitmap(newFace: Bitmap) {
        _face.value = newFace
    }

    fun clearFace() {
        _face.value = null
    }

}