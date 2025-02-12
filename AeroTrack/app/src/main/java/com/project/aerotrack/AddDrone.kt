package com.project.aerotrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddDrone : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_drone)
        val button = findViewById<AppCompatButton>(R.id.SubmitFinal)
        button.setOnClickListener{
            val intent = Intent(this, MapScreen::class.java)
            startActivity(intent)
        }
    }
}