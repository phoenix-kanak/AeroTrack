package com.project.aerotrack.models

data class RegisterDrone(
    val droneId: String,
    val droneModel: String,
    val landingPointLat: Double,
    val landingPointLong: Double,
    val landingTime: String,
    val purpose: String,
    val takeOffPointLat: Double,
    val takeOffPointLong: Double,
    val takeOffTime: String
)