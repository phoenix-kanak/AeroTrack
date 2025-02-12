package com.project.aerotrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class HomeActivity : AppCompatActivity() {
    private lateinit var sharedPrefManager:SharedPrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        sharedPrefManager=SharedPrefManager(this)
        val token=sharedPrefManager.getToken()
        Log.d("token","$token")
        if(token?.isNotEmpty() == true){
            val intent=Intent(this,OpenMapActivity::class.java)
            startActivity(intent)
            finish()
        }
        val signupBtn = findViewById<AppCompatButton>(R.id.SignUp)
        signupBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val loginBtn = findViewById<AppCompatButton>(R.id.LogIn)
        loginBtn.setOnClickListener{
            val intent=Intent(this , LoginActivity::class.java)
            startActivity(intent)
        }
    }
}