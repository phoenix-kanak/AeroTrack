package com.project.aerotrack.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.project.aerotrack.ApiInterface
import com.project.aerotrack.models.UserLoginRequest
import com.project.aerotrack.models.UserLoginResponse
import com.project.aerotrack.models.UserSignupRequest
import com.project.aerotrack.models.UserSignupResponse
import com.project.aerotrack.utils.NetworkClient
import com.project.aerotrack.utils.NetworkResult
import org.json.JSONObject

class AuthRepository(private val api: ApiInterface) {
    companion object {
        fun create(): ApiInterface {
            return NetworkClient().getRetrofit().create(ApiInterface::class.java)
        }
    }

    private val _signupResponseLiveData = MutableLiveData<NetworkResult<UserSignupResponse>>()

    val signupResponseLiveData: LiveData<NetworkResult<UserSignupResponse>>
        get() = _signupResponseLiveData

    private val _loginResponseLiveData = MutableLiveData<NetworkResult<UserLoginResponse>>()
    val loginResponseLiveData: LiveData<NetworkResult<UserLoginResponse>>
        get() = _loginResponseLiveData

    suspend fun userSignup(userSignupRequest: UserSignupRequest) {
        _signupResponseLiveData.postValue(NetworkResult.Loading())
        try {
            val response = api.userSignUp(userSignupRequest)

            if (response.isSuccessful && response.body() != null) {
                _signupResponseLiveData.postValue(NetworkResult.Success(response.body()))
                Log.d("signup1", response.toString())
            } else {
                val errorBody = response.errorBody()?.charStream()?.readText()
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errObj = JSONObject(errorBody)
                        Log.d("signup2", errObj.toString())
                        _signupResponseLiveData.postValue(NetworkResult.Error(errObj.getString("message")))
                    } catch (e: Exception) {
                        Log.d("signup2", errorBody)
                        _signupResponseLiveData.postValue(NetworkResult.Error(errorBody))
                    }
                } else {
                    _signupResponseLiveData.postValue(NetworkResult.Error("Unknown error occurred"))
                }
            }
        } catch (e: Exception) {
            Log.e("signup_error", "Exception: ${e.localizedMessage}")
            _signupResponseLiveData.postValue(NetworkResult.Error("Network error: ${e.localizedMessage}"))
        }
    }

    suspend fun userLogin(userLoginRequest: UserLoginRequest) {
        _loginResponseLiveData.postValue(NetworkResult.Loading())
        try {
            val response = api.userLogin(userLoginRequest)

            if (response.isSuccessful && response.body() != null) {
                _loginResponseLiveData.postValue(NetworkResult.Success(response.body()))
                Log.d("signup1", response.toString())
            } else {
                val errorBody = response.errorBody()?.charStream()?.readText()
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errObj = JSONObject(errorBody)
                        Log.d("signup2", errObj.toString())
                        _loginResponseLiveData.postValue(NetworkResult.Error(errObj.getString("message")))
                    } catch (e: Exception) {
                        Log.d("signup2", errorBody)
                        _loginResponseLiveData.postValue(NetworkResult.Error(errorBody))
                    }
                } else {
                    _loginResponseLiveData.postValue(NetworkResult.Error("Unknown error occurred"))
                }
            }
        } catch (e: Exception) {
            Log.e("signup_error", "Exception: ${e.localizedMessage}")
            _loginResponseLiveData.postValue(NetworkResult.Error("Network error: ${e.localizedMessage}"))
        }
    }
}