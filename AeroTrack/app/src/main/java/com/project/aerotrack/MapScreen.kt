package com.project.aerotrack

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference

class MapScreen : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_screen)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
       addDummyMarkers()
    }

    private fun addDummyMarkers() {
        mMap.clear() // Clear existing markers

        val dummyLocations = listOf(
            LatLng(28.6129, 77.2295), // India Gate
            LatLng(28.6315, 77.2167), // Connaught Place
            LatLng(28.6562, 77.2410), // Red Fort
            LatLng(28.5245, 77.1855), // Qutub Minar
            LatLng(28.5535, 77.2588), // Lotus Temple
            LatLng(28.6507, 77.2305), // Chandni Chowk
            LatLng(28.5562, 77.1000)  // Delhi Airport (IGI T3)
        )

        for ((index, location) in dummyLocations.withIndex()) {
            mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Drone ${index + 1}")
                    .icon(createCustomMarker("Drone ${index + 1}"))
            )
            Log.d("MapScreen", "Added marker: Drone ${index + 1} at $location")
        }

        // Move camera to the first marker
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dummyLocations[0], 15f))
    }

    private fun createCustomMarker(text: String): BitmapDescriptor {
        // Inflate the custom marker layout
        val markerView = LayoutInflater.from(this).inflate(R.layout.custom_marker, null)

        // Get the ImageView and TextView
        val markerIcon = markerView.findViewById<ImageView>(R.id.marker_icon)
        val markerTextView = markerView.findViewById<TextView>(R.id.marker_text)

        // Set text for the marker
        markerTextView.text = text

        // Set a default image resource or a drawable that works
        markerIcon.setImageResource(R.drawable.pngtree_drone_flying_on_transparent_background_png_image_6209864)

        // Ensure the layout is measured correctly
        markerView.measure(
            View.MeasureSpec.makeMeasureSpec(150, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(150, View.MeasureSpec.EXACTLY)
        )
        markerView.layout(0, 0, 100, 100)

        // Log the measured width and height
        Log.d("MapScreen", "Measured Width: ${markerView.measuredWidth}, Measured Height: ${markerView.measuredHeight}")

        // Create bitmap from the custom layout
        val bitmap = Bitmap.createBitmap(markerView.measuredWidth, markerView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)

        // Return the BitmapDescriptor for the custom marker
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}