package com.project.aerotrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.aerotrack.databinding.ActivityLoginBinding
import com.project.aerotrack.models.UserLoginRequest
import com.project.aerotrack.repository.AuthRepository
import com.project.aerotrack.utils.NetworkResult
import com.project.aerotrack.viewmodels.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPrefManager
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sharedPreferences = SharedPrefManager(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val apiInterface = AuthRepository.create()
        val authRepo = AuthRepository(apiInterface)
        authViewModel = AuthViewModel(authRepo)
        binding.submit.setOnClickListener {
            val userID = binding.userID.text.trim().toString()
            val password = binding.loginPassword.text.trim().toString()

            val loginInfo = UserLoginRequest(password, userID)
            authViewModel.login(loginInfo)
            bindObserver(userID)
        }
    }

    fun bindObserver(userID: String) {
        authViewModel.loginLiveData.observe(this, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data?.message == "Contact the super admin / admin") {
                        Toast.makeText(this, "${it.data?.message} ", Toast.LENGTH_SHORT).show()
                    } else {
                        sharedPreferences.saveToken("${it.data?.token}")
                        saveUserIdToFirestore(userID)
                        val intent = Intent(this, OpenMapActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {
                    binding.progressBar.isVisible = true
                }

                else -> {
//                    Toast.makeText(this , "Timeout Error",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun saveUserIdToFirestore(userID: String) {
        val userMap = hashMapOf(
            "userID" to userID
        )
        sharedPreferences.saveUserName(userID)
        Log.d("messageSent",userID)
        db.collection("all_users").document(userID).set(userMap).addOnSuccessListener {
            Log.d("Firestore", "User ID stored successfully")
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error storing user ID", e)
        }
    }
}