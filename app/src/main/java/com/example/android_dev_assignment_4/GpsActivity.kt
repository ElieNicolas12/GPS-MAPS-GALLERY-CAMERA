package com.example.android_dev_assignment_4

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONObject
import kotlin.properties.Delegates


class GpsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    var listLoc: MutableList<Details> = mutableListOf()
    private var markerPerth: Marker? = null
    private lateinit var mMap:GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var longitude by Delegates.notNull<Double>()
    private var latitude by Delegates.notNull<Double>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps)
        val mapFragment=supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)





    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap=googleMap
        mMap.uiSettings.isZoomControlsEnabled=true
        setUpMap()
        mMap.setOnMarkerClickListener (this)

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                latitude=location.latitude
                longitude=location.longitude
                val currentlatlong = LatLng(location.latitude, location.longitude)
                markerPerth = mMap.addMarker(
                    MarkerOptions()
                        .position(currentlatlong)
                        .title("CurrentLocation")
                )
                markerPerth?.tag=0

                //placeMakerOnMapnew(currentlatlong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatlong, 12f))
                var name = findViewById<EditText>(R.id.Name)
                var det: MutableList<Double> = mutableListOf()
                det.add(location.latitude)
                det.add(location.longitude)
                if (name.text != null) {
                    findViewById<Button>(R.id.save).setOnClickListener()
                    {
                        PostAPI(det, name.text.toString())
                    }
                }

                var OldLoc: LatLng
                for (Details in listLoc) {
                    var lat: Double = Details.values[0]
                    var long: Double = Details.values[1]
                    OldLoc = LatLng(lat, long)
                    placeMakerOnMap(OldLoc, Details.Name)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(OldLoc, 12f))

                }
            }
        }
    }
    private fun placeMakerOnMap(OldLoc: LatLng,Name: String) {
        val markerOptions= MarkerOptions().position(OldLoc)
        markerOptions.title("$Name \n $OldLoc")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        mMap.addMarker(markerOptions)

    }


 //   override fun onMarkerClick(p0: Marker) = false
    override fun onMarkerClick(marker: Marker): Boolean {


     val builder = AlertDialog.Builder(this)
     builder.setTitle("Pin ${marker.id}")
     //set message for alert dialog
     builder.setMessage("Do you want to delete pin with \n latitude: $latitude and longitude: $longitude")
     builder.setIcon(android.R.drawable.ic_dialog_alert)
     builder.setPositiveButton("Yes") { dialogInterface, which ->
         Toast.makeText(applicationContext, marker.id.substring(1, 2), Toast.LENGTH_LONG).show()

         //delete(2)
         val m = marker.id

         marker.remove()
     }
     builder.setNegativeButton("No") { dialogInterface, which ->
         Toast.makeText(applicationContext, "clicked No", Toast.LENGTH_LONG).show()
     }
     val alertDialog: AlertDialog = builder.create()
     alertDialog.setCancelable(false)
     alertDialog.show()



     return false
 }


    data class Details(var values: MutableList<Double>, var Name: String) {
        override fun toString(): String {
            var result: String = ""
            result += "[ "
            for (item in values) {
                result += item
                result += "  "
            }
            result += "] Name: " + Name

            return result
        }
    }

    private fun PostAPI(Values : MutableList<Double>, Name: String)
    {
        val url = "https://617d692d1eadc500171364fd.mockapi.io/Valeur"
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, JSONObject("{\"V\": ${Values}, \"D\": ${Name}}"),
            { response -> println(response) },
            { error -> error.printStackTrace() })
        queue.add(jsonObjectRequest)

    }
    private fun DeleteAPI(Values : MutableList<Double>, Name: String)
    {
        val url = "https://617d692d1eadc500171364fd.mockapi.io/Valeur"
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.DELETE, url, JSONObject("{\"V\": ${Values}, \"D\": ${Name}}"),
            { response -> println(response) },
            { error -> error.printStackTrace() })
        queue.add(jsonObjectRequest)

    }
    private fun get() {
        var list: MutableList<Details> = mutableListOf()
        val queue: RequestQueue = Volley.newRequestQueue(this)
        val url = "https://617d692d1eadc500171364fd.mockapi.io/Valeur"

        val postRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("Resp1", response.toString())
                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    val diceArray: JSONArray = jsonObject.getJSONArray("V")
                   val diceMutableList: MutableList<Double> = mutableListOf()
                    for (j in 0 until diceArray.length()) {

                        diceMutableList.add(diceArray.get(j) as Double)


                    }
                    val Dd=jsonObject.getString("D")


                   list.add(Details(diceMutableList, Dd))
                }

            },
            { error ->
                Log.d("Error", error.toString())
            })

        queue.add(postRequest)

    }



}



