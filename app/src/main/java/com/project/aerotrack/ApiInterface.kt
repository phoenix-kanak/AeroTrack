package com.project.aerotrack

import com.project.aerotrack.models.DroneResponse
import com.project.aerotrack.models.RegisterDrone
import com.project.aerotrack.models.RegisterDroneResponse
import com.project.aerotrack.models.UserLoginRequest
import com.project.aerotrack.models.UserLoginResponse
import com.project.aerotrack.models.UserSignupRequest
import com.project.aerotrack.models.UserSignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @POST("user/signup")
    suspend fun userSignUp(@Body userSignupRequest: UserSignupRequest): Response<UserSignupResponse>

    @POST("drone/register")
    suspend fun registerDrone(@Header("Authorization") token:String , @Body registerDrone: RegisterDrone) : Response<RegisterDroneResponse>

    @POST("user/userlogin")
    suspend fun userLogin(@Body userLoginRequest: UserLoginRequest) : Response<UserLoginResponse>

    @GET("drone/drones")
    suspend fun getAllDrone(@Header("Authorization") token: String) :Response<DroneResponse>

}