package com.project.aerotrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.project.aerotrack.models.UserSignupRequest
import com.project.aerotrack.repository.AuthRepository
import com.project.aerotrack.utils.NetworkResult
import com.project.aerotrack.viewmodels.AuthViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val apiInterface = AuthRepository.create()
        val authRepo = AuthRepository(apiInterface)
        authViewModel = AuthViewModel(authRepo)
        val button = findViewById<AppCompatButton>(R.id.submit)
        button.setOnClickListener {
            val rank = findViewById<EditText>(R.id.rank).text.toString()
            val name = findViewById<EditText>(R.id.name).text.trim().toString()
            val userID = findViewById<EditText>(R.id.user_id).text.trim().toString()
            val password = findViewById<EditText>(R.id.password).text.toString()
            val unit = findViewById<EditText>(R.id.unit).text.toString()
            val email = findViewById<EditText>(R.id.email).text.toString()
            val contactNo = findViewById<EditText>(R.id.phone_no).text.trim().toString()
            val contact: Long = contactNo.toLong()
            val role = findViewById<EditText>(R.id.role).text.toString()
            val strength = findViewById<EditText>(R.id.strength).text.toString()

            if (userID.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter UserID and Password", Toast.LENGTH_SHORT)
                    .show()
            }
            if (contact.toString().length != 10) {
                Toast.makeText(this, "Please enter a valid contact number", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val userRequest = UserSignupRequest(
                    email,
                    contact,
                    name,
                    password,
                    rank,
                    role,
                    strength,
                    unit,
                    userID
                )
                Log.d("signup", "$userRequest")
                authViewModel.signup(userRequest)
                bindObserver()
            }
        }
    }

    fun bindObserver() {
        authViewModel.signupLiveData.observe(this, Observer {
            findViewById<ProgressBar>(R.id.progress_bar).isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {
                    findViewById<ProgressBar>(R.id.progress_bar).isVisible = true
                }

                else -> {
//                    Toast.makeText(this , "Timeout Error",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}