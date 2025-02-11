package com.djc.ft_testingapp.ui.facetrack

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djc.ft_testingapp.domain.model.UserModel
import com.djc.ft_testingapp.domain.usecase.RecognitionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecognitionViewModel @Inject constructor(
    private val recognitionUseCase : RecognitionUseCase
) : ViewModel(){

    private var ultimaLlamada: Long = 0L
    private val intervaloMinimo = 3000L //Cadencia de 3s

    private val _face = MutableLiveData<Bitmap?>()
    val face: LiveData<Bitmap?> get() = _face

    private val _user = MutableLiveData<UserModel>(null)
    val user : LiveData<UserModel> get()= _user

    fun getFaceInfo(){
        val tiempoActual = System.currentTimeMillis()
        if (tiempoActual - ultimaLlamada >= intervaloMinimo) {
            ultimaLlamada = tiempoActual
            viewModelScope.launch {
                if (face.value!=null){
                    _user.value = recognitionUseCase.invoke(_face.value!!)
                }
            }
        }
    }

    fun setBitmap(newFace: Bitmap) {
        _face.value = newFace
    }

}