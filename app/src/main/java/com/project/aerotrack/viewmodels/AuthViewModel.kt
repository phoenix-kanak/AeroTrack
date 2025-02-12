package com.project.aerotrack.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.aerotrack.models.UserLoginRequest
import com.project.aerotrack.models.UserSignupRequest
import com.project.aerotrack.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository):ViewModel() {
    val signupLiveData = repository.signupResponseLiveData
    val loginLiveData = repository.loginResponseLiveData

    fun signup(userSignupRequest: UserSignupRequest){
        viewModelScope.launch {
            repository.userSignup(userSignupRequest)
        }
    }
    fun login(userLoginRequest: UserLoginRequest){
        viewModelScope.launch {
            repository.userLogin(userLoginRequest)
        }
    }
}