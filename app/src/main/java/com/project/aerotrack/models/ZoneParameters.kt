package com.project.aerotrack.models

data class ZoneParameters(
    val zone: String,
    val centralMeridian: Double,
    val latOrigin: Double,
    val scaleFactor: Double,
    val falseEasting: Double,
    val falseNorthing: Double
)

