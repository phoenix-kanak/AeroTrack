package com.project.aerotrack

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.DatabaseReference
import com.project.aerotrack.databinding.ActivityMapScreenBinding
import com.project.aerotrack.models.Drones
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class MapScreen : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityMapScreenBinding
    private var allDrones: ArrayList<Drones>? = null  // Store drones safely
    private var currentPolyline: Polyline? = null
    private var takeOffCircle: Circle? = null
    private var landingCircle: Circle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMapScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val rawList = intent.getSerializableExtra("drones") as? ArrayList<*>
        val itemList = rawList?.filterIsInstance<Drones>()?.let { ArrayList(it) }
        allDrones = itemList
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        addDummyMarkers()
        mMap.setOnMarkerClickListener { marker ->
            val drone = allDrones?.find {
                it.takeOffPointLat == marker.position.latitude && it.takeOffPointLong == marker.position.longitude
            }
            if (drone != null) {
                showDronePath(drone)
            }
            false
        }
    }

    private fun showDronePath(drone: Drones) {
        // Clear the previous polyline if any
        currentPolyline?.remove()
        landingCircle?.remove()
        takeOffCircle?.remove()

        val takeoffLocation = LatLng(drone.takeOffPointLat, drone.takeOffPointLong)
        val landingLocation = LatLng(drone.landingPointLat, drone.landingPointLong)

        // Draw a new polyline from takeoff to landing
        currentPolyline = mMap.addPolyline(
            PolylineOptions()
                .add(takeoffLocation, landingLocation)
                .width(5f)
                .color(Color.RED)
        )

        // Add black dots at both ends
        takeOffCircle = mMap.addCircle(
            CircleOptions()
                .center(takeoffLocation)
                .radius(10.0)
                .strokeColor(Color.BLACK)
                .fillColor(Color.BLACK)
        )
        landingCircle = mMap.addCircle(
            CircleOptions()
                .center(landingLocation)
                .radius(10.0)
                .strokeColor(Color.BLACK)
                .fillColor(Color.BLACK)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDummyMarkers() {
        mMap.clear()
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))
        allDrones?.let { droneList ->
            for ((index, drone) in droneList.withIndex()) {

                val droneLocation = LatLng(drone.landingPointLat, drone.landingPointLong)
                val landingPoint = LatLng(drone.takeOffPointLat, drone.takeOffPointLong)
                val marker = mMap.addMarker(
                    MarkerOptions().position(landingPoint).title("Drone ID: ${drone.droneId}")
                        .snippet("Landing Point: (${drone.landingPointLat}, ${drone.landingPointLong})")
                        .icon(createCustomMarker("Drone ${index + 1}"))
                )
                marker?.tag =
                    "Takeoff Time: ${convertTo12HourFormat(drone.takeOffTime)}\n Landing Time: ${
                        convertTo12HourFormat(drone.landingTime)
                    }\n "

                Log.d("MapScreen", "Added marker: Drone ${index + 1} at $landingPoint")
            }

            val latLng = allDrones?.lastOrNull()?.let { drone ->
                drone.takeOffPointLat?.let { lat ->
                    drone.takeOffPointLong?.let { lng ->
                        LatLng(lat, lng)
                    }
                }
            }
            if (latLng != null) mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    private fun createCustomMarker(text: String): BitmapDescriptor {
        // Inflate the custom marker layout
        val markerView = LayoutInflater.from(this).inflate(R.layout.custom_marker, null)

        // Get the ImageView and TextView
        val markerIcon = markerView.findViewById<ImageView>(R.id.marker_icon)


        // Set a default image resource or a drawable that works
        markerIcon.setImageResource(R.drawable.pngtree_drone_flying_on_transparent_background_png_image_6209864)

        // Ensure the layout is measured correctly
        markerView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        // Retrieve the measured dimensions
        val width = markerView.measuredWidth
        val height = markerView.measuredHeight

        // Layout the view with its measured dimensions
        markerView.layout(0, 0, width, height)
        // Log the measured width and height
        Log.d(
            "MapScreen",
            "Measured Width: ${markerView.measuredWidth}, Measured Height: ${markerView.measuredHeight}"
        )

        // Create bitmap from the custom layout
        val bitmap = Bitmap.createBitmap(
            markerView.measuredWidth, markerView.measuredHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)

        // Return the BitmapDescriptor for the custom marker
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertTo12HourFormat(isoTime: String): String {
        val instant = Instant.parse(isoTime)  // Parse ISO timestamp

        // Convert to local time zone (UTC in this case)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.of("UTC"))

        val formattedTime = formatter.format(instant)
        return formattedTime
    }

}

class CustomInfoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter {

    private val inflater = LayoutInflater.from(context)

    override fun getInfoContents(marker: Marker): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.layout_drone_info, null)

        // Retrieve views from the layout
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvSnippet = view.findViewById<TextView>(R.id.tvSnippet)
        val tvExtraDetail = view.findViewById<TextView>(R.id.tvExtraDetail)
        tvExtraDetail.text = marker.tag.toString()
        // Set the title and snippet from marker properties
        tvTitle.text = marker.title
        tvSnippet.text = marker.snippet

        // If you have extra details, you can store them in the marker’s tag.
        // For example, if marker.tag is a data object with more details:

        return view
    }

    override fun getInfoWindow(marker: Marker): View? {
        // Return null so that getInfoContents() is used.
        return null
    }
}