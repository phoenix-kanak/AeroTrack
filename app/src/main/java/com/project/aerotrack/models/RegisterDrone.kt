package com.project.aerotrack.models

data class RegisterDrone(
    val droneId: String = "",
    val droneModel: String = "",
    val landingPointLat: Double = 0.0,
    val landingPointLong: Double = 0.0,
    val landingTime: String = "",
    val purpose: String = "",
    val takeOffPointLat: Double = 0.0,
    val takeOffPointLong: Double = 0.0,
    val takeOffTime: String = ""
)
