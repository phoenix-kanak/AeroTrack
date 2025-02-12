package com.project.aerotrack.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.project.aerotrack.ApiInterface
import com.project.aerotrack.models.DroneResponse
import com.project.aerotrack.models.RegisterDrone
import com.project.aerotrack.models.RegisterDroneResponse
import com.project.aerotrack.utils.NetworkClient
import com.project.aerotrack.utils.NetworkResult
import org.json.JSONObject

class DroneRepository(private val api: ApiInterface) {
    companion object {
        fun create(): ApiInterface {
            return NetworkClient().getRetrofit().create(ApiInterface::class.java)
        }
    }

    private val _droneRegisterLiveData = MutableLiveData<NetworkResult<RegisterDroneResponse>>()
    private val _allDronesLiveData = MutableLiveData<NetworkResult<DroneResponse>>()
    val allDronesLiveData: LiveData<NetworkResult<DroneResponse>>
        get() = _allDronesLiveData
    val droneRegisterLiveData: LiveData<NetworkResult<RegisterDroneResponse>>
        get() = _droneRegisterLiveData

    suspend fun registerDrone(token: String, registerDrone: RegisterDrone) {
        _droneRegisterLiveData.postValue(NetworkResult.Loading())

        try {
            val response = api.registerDrone(token, registerDrone)

            if (response.isSuccessful && response.body() != null) {
                _droneRegisterLiveData.postValue(NetworkResult.Success(response.body()))
                Log.d("signup1", response.toString())
            } else {
                val errorBody = response.errorBody()?.charStream()?.readText()
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errObj = JSONObject(errorBody)
                        Log.d("signup2", errObj.toString())
                        _droneRegisterLiveData.postValue(NetworkResult.Error(errObj.getString("message")))
                    } catch (e: Exception) {
                        Log.d("signup2", errorBody)
                        _droneRegisterLiveData.postValue(NetworkResult.Error(errorBody))
                    }
                } else {
                    _droneRegisterLiveData.postValue(NetworkResult.Error("Unknown error occurred"))
                }
            }
        } catch (e: Exception) {
            Log.e("signup_error", "Exception: ${e.localizedMessage}")
            _droneRegisterLiveData.postValue(NetworkResult.Error("Network error: ${e.localizedMessage}"))
        }
    }

    suspend fun getAllDrones(token: String) {
        _allDronesLiveData.postValue(NetworkResult.Loading())

        try {
            val response = api.getAllDrone(token)

            if (response.isSuccessful && response.body() != null) {
                _allDronesLiveData.postValue(NetworkResult.Success(response.body()))
                Log.d("signup1", response.toString())
            } else {
                val errorBody = response.errorBody()?.charStream()?.readText()
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errObj = JSONObject(errorBody)
                        Log.d("signup2", errObj.toString())
                        _allDronesLiveData.postValue(NetworkResult.Error(errObj.getString("message")))
                    } catch (e: Exception) {
                        Log.d("signup2", errorBody)
                        _allDronesLiveData.postValue(NetworkResult.Error(errorBody))
                    }
                } else {
                    _allDronesLiveData.postValue(NetworkResult.Error("Unknown error occurred"))
                }
            }
        } catch (e: Exception) {
            Log.e("signup_error", "Exception: ${e.localizedMessage}")
            _allDronesLiveData.postValue(NetworkResult.Error("Network error: ${e.localizedMessage}"))
        }
    }
}