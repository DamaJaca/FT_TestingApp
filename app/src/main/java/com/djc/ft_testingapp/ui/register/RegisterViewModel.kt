package com.djc.ft_testingapp.ui.register

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djc.ft_testingapp.domain.Repository
import com.djc.ft_testingapp.domain.usecase.FaceCRUDUseCase
import com.djc.ft_testingapp.domain.model.UserModel
import com.djc.ft_testingapp.domain.usecase.RecognitionUseCase
import com.djc.ft_testingapp.domain.usecase.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val recognitionUseCase: RecognitionUseCase,
    private val saveUserUseCase: SaveUserUseCase
) :ViewModel(){

    private val _error = MutableLiveData<String>(null)
    val error : LiveData<String> get()= _error

    private val _user = MutableLiveData<UserModel>(null)
    val user : LiveData<UserModel> get()= _user

    fun getFaceInfo(bitmap: Bitmap){
        viewModelScope.launch {
            _user.value = recognitionUseCase.invoke(bitmap)
        }
    }

    fun saveUserInfo(name :String, age : String, sex : String){
        viewModelScope.launch {
            saveUserUseCase(UserModel(user.value!!.id, name, age, sex))
        }
    }



}