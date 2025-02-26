package com.project.aerotrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.aerotrack.databinding.ActivityAddDroneBinding
import com.project.aerotrack.models.RegisterDrone
import com.project.aerotrack.repository.DroneRepository
import com.project.aerotrack.utils.ConversionUtil
import com.project.aerotrack.utils.NetworkResult
import com.project.aerotrack.viewmodels.DroneViewModel

class AddDrone : AppCompatActivity() {
    private lateinit var binding: ActivityAddDroneBinding
    private lateinit var droneViewModel: DroneViewModel
    private lateinit var sharedPref: SharedPrefManager
    private lateinit var firebaseDb:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sharedPref = SharedPrefManager(this)

        binding = ActivityAddDroneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val apiInterface = DroneRepository.create()
        val authRepo = DroneRepository(apiInterface)
        droneViewModel = DroneViewModel(authRepo)
        firebaseDb = FirebaseDatabase.getInstance().reference

        binding.takeoffTime.setOnClickListener {
            showMaterialTimePicker(binding.takeoffTime)
        }
        binding.landingTime.setOnClickListener {
            showMaterialTimePicker(binding.landingTime)
        }

        val zones = resources.getStringArray(R.array.zones)
        val arrayAdapter = ArrayAdapter(this ,R.layout.item_zone, zones)
        binding.landingPointZone.setAdapter(arrayAdapter)
        binding.takeoffPointZone.setAdapter(arrayAdapter)
        val button = findViewById<AppCompatButton>(R.id.SubmitFinal)
        button.setOnClickListener {
            val droneModel = findViewById<EditText>(R.id.drone_model).text.toString()
            val droneId = findViewById<EditText>(R.id.drone_id).text.toString()
            val purpose = findViewById<EditText>(R.id.purpose).text.toString()
            val takeOffTime = findViewById<TextView>(R.id.takeoff_time).text.toString()
            val landingTime = findViewById<TextView>(R.id.landing_time).text.toString()
            val takeOffPointEasting =
                findViewById<EditText>(R.id.takeoff_point_easting).text.toString()
            val takeOffPointNorthing =
                findViewById<EditText>(R.id.takeoff_point_northing).text.toString()
            val takeOffPointZone = findViewById<Spinner>(R.id.takeoff_point_zone).selectedItem.toString()
            val landingPointEasting =
                findViewById<EditText>(R.id.landing_point_easting).text.toString()
            val landingPointNorthing =
                findViewById<EditText>(R.id.landing_point_northing).text.toString()
            val landingPointZone = findViewById<Spinner>(R.id.landing_point_zone).selectedItem.toString()
            if (droneModel.isEmpty() || droneId.isEmpty() || purpose.isEmpty() || takeOffTime.isEmpty() || landingTime.isEmpty() || landingPointEasting.isEmpty() || landingPointNorthing.isEmpty() || landingPointZone.isEmpty() || takeOffPointEasting.isEmpty() || takeOffPointNorthing.isEmpty() || takeOffPointZone.isEmpty()) {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            } else {
                val (takeOffPointLat, takeOffPointLong) = ConversionUtil.convertFromEPSG24378Locally(
                    takeOffPointEasting.toDouble(),
                    takeOffPointNorthing.toDouble(),
                    takeOffPointZone
                )
                val (landingPointLat, landingPointLong) = ConversionUtil.convertFromEPSG24378Locally(
                    landingPointEasting.toDouble(),
                    landingPointNorthing.toDouble(),
                    landingPointZone
                )
                Log.d("lat long", "$takeOffPointLat $takeOffPointLong")
//                0.005808526586029959 88.5173213587982

                val droneInfo = RegisterDrone(
                    droneId,
                    droneModel,
                    landingPointLat,
                    landingPointLong,
                    landingTime,
                    purpose,
                    takeOffPointLat,
                    takeOffPointLong,
                    takeOffTime
                )

                val token = sharedPref.getToken()
                Log.d("signup","$droneInfo")
                saveDroneData(droneInfo)
//                droneViewModel.registerDrone("Bearer $token", droneInfo)
//                bindObserver()
            }
        }
    }

    fun bindObserver() {
        droneViewModel.registerDroneLiveData.observe(this, Observer {
            findViewById<ProgressBar>(R.id.drone_progress).isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    Toast.makeText(this, "Drone Added", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, OpenMapActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {
                    findViewById<ProgressBar>(R.id.drone_progress).isVisible = true
                }

                else -> {}
            }
        })
    }

    fun convertIGRSToLatLong(
        igrsNorthing: Double,
        igrsEasting : Double,
        scaleFactor: Double = 0.9999,
        falseEasting: Double = 0.0,
        falseNorthing: Double = 0.0,
        centralMeridian: Double = Math.toRadians(77.0)
    ):  Pair<Double ,Double > {
        // Ellipsoid parameters for the Everest 1830 ellipsoid (approximate values)
        val a = 6377276.345 // Semi-major axis (meters)
        val b = 6356075.413 // Semi-minor axis (meters)
        val eSquared = (a * a - b * b) / (a * a) // Eccentricity squared

        // Remove the false offsets
        val x = igrsEasting - falseEasting
        val y = igrsNorthing - falseNorthing

        // --- Simplified (and approximate) inverse conversion ---
        // In a complete implementation, you would:
        //  1. Compute the footpoint latitude from the meridional arc.
        //  2. Use iterative methods to refine the latitude.
        //  3. Correct for the convergence of meridians, etc.
        //
        // For demonstration, we use a simple (and not accurate) estimation:
        val latRadians = y / (a * scaleFactor)   // VERY simplified estimation
        val lonRadians = centralMeridian + x / (a * scaleFactor)

        // Convert radians to degrees
        val latitude = Math.toDegrees(latRadians)
        val longitude = Math.toDegrees(lonRadians)

        return Pair(latitude, longitude)
    }

    private fun showMaterialTimePicker(view: TextView) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)  // or TimeFormat.CLOCK_12H
            .setHour(12)                        // default hour
            .setMinute(0)                       // default minute
            .setTitleText("Select Time")
            .build()
        picker.show(supportFragmentManager, "MATERIAL_TIME_PICKER")

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            // Format the minute to always be two digits (e.g., "05" instead of "5")
            val formattedMinute = String.format("%02d", minute)
            // Update the EditText with the selected time
            view.setText("$hour:$formattedMinute")
        }
    }
    private fun saveDroneData(droneInfo: RegisterDrone) {
        val droneRef = firebaseDb.child("drones")

        droneRef.setValue(droneInfo)
            .addOnSuccessListener {
                Toast.makeText(this, "Drone data saved successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, OpenMapActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}