package com.project.aerotrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.project.aerotrack.databinding.ActivityOpenMapBinding
import com.project.aerotrack.repository.DroneRepository
import com.project.aerotrack.utils.NetworkResult
import com.project.aerotrack.viewmodels.DroneViewModel

class OpenMapActivity : AppCompatActivity() {
    private lateinit var droneViewModel: DroneViewModel
    private lateinit var sharedPref: SharedPrefManager
    private lateinit var binding: ActivityOpenMapBinding
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
            finish()
        }
    }
    fun bindObserver() {
        droneViewModel.getAllDrones.observe(this, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {

                    Log.d("All Drones", it.data?.message ?:"null")
                    Log.d("All Drones","${it.data?.drones}")
                    val allDrones=it.data?.drones
                    if(allDrones?.isEmpty() == true){
                        Toast.makeText(this , "No Drones Found", Toast.LENGTH_SHORT).show()
                    }else {
                        val intent = Intent(this, MapScreen::class.java)
                        intent.putExtra("drones", it.data?.drones)
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