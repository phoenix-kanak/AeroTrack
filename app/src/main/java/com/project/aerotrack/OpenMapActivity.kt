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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.aerotrack.databinding.ActivityOpenMapBinding
import com.project.aerotrack.models.DroneInfo
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
    private lateinit var databaseReference: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sharedPref = SharedPrefManager(this)
        binding = ActivityOpenMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
        } catch (e: Exception) {
//            textViewResult.text = "Conversion error: ${e.localizedMessage}"
        }
        val apiInterface = DroneRepository.create()
        val authRepo = DroneRepository(apiInterface)
        databaseReference = FirebaseDatabase.getInstance().reference
        droneViewModel = DroneViewModel(authRepo)
        binding.goToMaps.setOnClickListener {
            val token = sharedPref.getToken()
            droneViewModel.getAllDrones("Bearer $token")
//            bindObserver()
            val intent = Intent(this, MapScreen::class.java)
//            intent.putExtra("drones", finalDrones)
            startActivity(intent)
        }

        binding.addDrone.setOnClickListener {
            startActivity(Intent(this, AddDrone::class.java))
        }

        binding.goToChats.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        // Query the newest child by ordering by key (push keys are chronological) and limiting to last 1

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun bindObserver() {
        droneViewModel.getAllDrones.observe(this, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {

                    Log.d("All Drones", it.data?.message ?: "null")
                    Log.d("All Drones", "${it.data?.drones}")
                    val allDrones = it.data?.drones
                    val finalDrones = arrayListOf<Drones>()
                    if (allDrones != null) {
                        for (drone in allDrones) {
                            val instant = Instant.parse(drone.landingTime)
                            val zoneId = ZoneId.systemDefault()
                            val localDateTime = instant.atZone(zoneId).toLocalDateTime()
                            val currentTime = LocalDateTime.now(zoneId)
                            if (localDateTime.isAfter(currentTime)) {
                                finalDrones.add(drone)
                            }
                        }
                    }

                    if (finalDrones.isEmpty()) {
                        Toast.makeText(this, "No Active Drones Found", Toast.LENGTH_SHORT).show()
                    } else {
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
                    Toast.makeText(this, "Timeout Error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}