package com.project.aerotrack.models

data class UserLoginRequest(
    val password: String,
    val userId: String
)