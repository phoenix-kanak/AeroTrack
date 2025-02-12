package com.project.aerotrack.models

import java.io.Serializable


data class Drones(
    val droneId: String,
    val droneModel: String,
    val height: Int,
    val landingPointLat: Double,
    val landingPointLong: Double,
    val landingTime: String,
    val latitude: Int,
    val longitude: Int,
    val purpose: String,
    val takeOffPointLat: Double,
    val takeOffPointLong: Double,
    val takeOffTime: String
):Serializable