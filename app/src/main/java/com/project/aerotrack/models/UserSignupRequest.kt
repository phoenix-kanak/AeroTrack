package com.project.aerotrack.models

data class UserSignupRequest(
    val email: String,
    val mobile: Long,
    val name: String,
    val password: String,
    val rank: String,
    val role: String,
    val strengthOfFleet: String,
    val unit: String,
    val userId: String
)
