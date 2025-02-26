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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.project.aerotrack.databinding.ActivityMapScreenBinding
import com.project.aerotrack.models.DroneId
import com.project.aerotrack.models.DroneInfo
import com.project.aerotrack.models.Drones
import com.project.aerotrack.models.RegisterDrone
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class MapScreen : AppCompatActivity(), OnMapReadyCallback {
    private val droneMarkers = mutableMapOf<String, String>()
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapScreenBinding
    private var allDrones: ArrayList<Drones>? = null  // Store drones safely
    private var currentPolyline: Polyline? = null
    private var takeOffCircle: Circle? = null
    private var landingCircle: Circle? = null
    private lateinit var droneInfo: DroneInfo
    private lateinit var droneId: RegisterDrone
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMapScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance().reference
        val rawList = intent.getSerializableExtra("drones") as? ArrayList<*>
        val itemList = rawList?.filterIsInstance<Drones>()?.let { ArrayList(it) }
        allDrones = itemList
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        databaseReference.child("drones").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val drone = snapshot.getValue(RegisterDrone::class.java)
                Log.d("droneInfo", "$drone")
                if (drone != null) {
                    droneId = drone
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("error", error.toString())
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val listener =
            databaseReference.child("gps_data").orderByKey().limitToLast(1)
        listener.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val drone = it.getValue(DroneInfo::class.java)
                    Log.d("droneInfo1", "$drone")
                    if (drone != null) {
                        droneInfo = drone
                        Log.d("DroneInfo", "0")
                        addDummyMarkers()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("message", error.toString())
            }
        })
//        mMap.setOnMarkerClickListener { marker ->


//            false
//        }
    }

    private fun showDronePath(drone: RegisterDrone) {
        // Clear the previous polyline if any
//        currentPolyline?.remove()
//        landingCircle?.remove()
//        takeOffCircle?.remove()

        val takeoffLocation = LatLng(drone.takeOffPointLat, drone.takeOffPointLong)
        val landingLocation = LatLng(drone.landingPointLat, drone.landingPointLong)

        // Draw a new polyline from takeoff to landing
        currentPolyline = mMap.addPolyline(
            PolylineOptions().add(takeoffLocation, landingLocation).width(80f).color(Color.RED)
        )

        // Add black dots at both ends
        takeOffCircle = mMap.addCircle(
            CircleOptions().center(LatLng(28.4402, 77.0712)).radius(100.0).strokeColor(Color.BLACK)
                .fillColor(Color.BLACK)
        )
        landingCircle = mMap.addCircle(
            CircleOptions().center(landingLocation).radius(100.0).strokeColor(Color.BLACK)
                .fillColor(Color.BLACK)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDummyMarkers() {
        Log.d("DroneInfo", "1")
        mMap.clear()
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))

//                val droneLocation = LatLng(dr.landingPointLat, drone.landingPointLong)
        val landingPoint = LatLng(droneId.takeOffPointLat, droneId.takeOffPointLong)
        val currentPos = LatLng(droneInfo.latitude, droneInfo.longitude)
        val marker = mMap.addMarker(
            MarkerOptions().position(currentPos).title("Drone ID: ${droneId.droneId}")
                .snippet("Landing Point: (${droneId.landingPointLat}, ${droneId.landingPointLong})")
                .icon(createCustomMarker("Drone"))
        )
        val tagString = buildString {
            append("Takeoff Time: ${droneId.takeOffTime}\n")
            append("Landing Time: ${droneId.landingTime}\n")
            append("Altitude: ${droneInfo.altitude}\n")
            append("Latitude: ${droneInfo.latitude}\n")
            append("Longitude: ${droneInfo.longitude}\n")
            append("Speed: ${droneInfo.speed}\n")
        }

        marker?.tag = tagString
        showDronePath(droneId)
        Log.d("DroneInfo", currentPos.toString())
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 15f))

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
        val formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.of("UTC"))

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