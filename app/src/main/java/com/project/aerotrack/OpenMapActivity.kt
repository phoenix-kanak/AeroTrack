package com.project.aerotrack

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.project.aerotrack.databinding.ActivityOpenMapBinding
import com.project.aerotrack.models.Drones
import com.project.aerotrack.repository.DroneRepository
import com.project.aerotrack.utils.NetworkResult
import com.project.aerotrack.viewmodels.DroneViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class OpenMapActivity : AppCompatActivity() {
    private lateinit var droneViewModel: DroneViewModel
    private lateinit var sharedPref: SharedPrefManager
    private lateinit var binding: ActivityOpenMapBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sharedPref = SharedPrefManager(this)
        binding=ActivityOpenMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
        } catch (e: Exception) {
//            textViewResult.text = "Conversion error: ${e.localizedMessage}"
        }
        val apiInterface = DroneRepository.create()
        val authRepo = DroneRepository(apiInterface)
        droneViewModel = DroneViewModel(authRepo)
        binding.goToMaps.setOnClickListener {
            val token = sharedPref.getToken()
            droneViewModel.getAllDrones("Bearer $token")
            bindObserver()
        }

        binding.addDrone.setOnClickListener {
            startActivity(Intent(this , AddDrone::class.java))
        }

        binding.goToChats.setOnClickListener{
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun bindObserver() {
        droneViewModel.getAllDrones.observe(this, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {

                    Log.d("All Drones", it.data?.message ?:"null")
                    Log.d("All Drones","${it.data?.drones}")
                    val allDrones=it.data?.drones
                    val finalDrones = arrayListOf<Drones>()
                    if (allDrones != null) {
                        for(drone in allDrones){
                            val instant = Instant.parse(drone.landingTime)
                            val zoneId = ZoneId.systemDefault()
                            val localDateTime = instant.atZone(zoneId).toLocalDateTime()
                            val currentTime = LocalDateTime.now(zoneId)
                            if(localDateTime.isAfter(currentTime)){
                                finalDrones.add(drone)
                            }
                        }
                    }

                    if(finalDrones.isEmpty()){
                        Toast.makeText(this , "No Active Drones Found", Toast.LENGTH_SHORT).show()
                    }else {
                        val intent = Intent(this, MapScreen::class.java)
                        intent.putExtra("drones", finalDrones)
                        startActivity(intent)
                    }

                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {
                    binding.progressBar.isVisible = true
                }

                else -> {
                    Toast.makeText(this , "Timeout Error",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}