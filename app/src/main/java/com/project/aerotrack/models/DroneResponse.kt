package com.project.aerotrack.models

import java.util.ArrayList

data class DroneResponse(
    val drones: ArrayList<Drones>,
    val message: String
)