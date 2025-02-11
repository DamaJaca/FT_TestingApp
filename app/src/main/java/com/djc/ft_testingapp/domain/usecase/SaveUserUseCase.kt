package com.djc.ft_testingapp.domain.usecase

import android.graphics.Bitmap
import com.djc.ft_testingapp.domain.Repository
import com.djc.ft_testingapp.domain.model.UserModel
import org.tensorflow.lite.Interpreter
import javax.inject.Inject

class SaveUserUseCase @Inject constructor (
    private val repository: Repository,
    private val interpreter: Interpreter
){

    suspend operator fun invoke(userModel: UserModel) {
        repository.saveUser(userModel)
    }
}