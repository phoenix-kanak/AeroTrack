package com.project.aerotrack.models

data class DroneInfo(
    val altitude:Double=0.0,
    val latitude:Double=0.0,
    val longitude:Double=0.0,
    val speed:Double=0.0,
    val timestamp:String=""
)
