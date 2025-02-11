package com.djc.ft_testingapp.ui.facetrack

import androidx.lifecycle.ViewModel
import com.djc.ft_testingapp.domain.usecase.RecognitionUseCase
import javax.inject.Inject

class RecognitionViewModel @Inject constructor(private val usecase : RecognitionUseCase) : ViewModel(){

}