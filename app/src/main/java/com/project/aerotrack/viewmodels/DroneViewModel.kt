package com.project.aerotrack.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.aerotrack.models.RegisterDrone
import com.project.aerotrack.repository.DroneRepository
import kotlinx.coroutines.launch

class DroneViewModel(private val droneRepository: DroneRepository):ViewModel() {
    val registerDroneLiveData = droneRepository.droneRegisterLiveData

    val getAllDrones=droneRepository.allDronesLiveData
    fun registerDrone(token:String , registerDrone: RegisterDrone){
        viewModelScope.launch {
            droneRepository.registerDrone(token , registerDrone)
        }
    }
    fun getAllDrones(token:String){
        viewModelScope.launch {
            droneRepository.getAllDrones(token)
        }
    }
}