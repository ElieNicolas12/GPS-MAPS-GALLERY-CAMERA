package com.example.android_dev_assignment_4

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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


class GpsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    var listLoc: MutableList<Details> = mutableListOf()

    private lateinit var mMap:GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient


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
        mMap.setOnMarkerClickListener (this)
        setUpMap()

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
                val currentlatlong = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(currentlatlong).title("Current Location"))
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
                listLoc=get()
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

    override fun onMarkerClick(p0: Marker) = false


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
    private fun get(): MutableList<Details> {
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

                    // textView.append( diceArray.toString() +"\n")
                    list.add(Details(diceMutableList, Dd))
                }

            },
            { error ->
                Log.d("Error", error.toString())
            })

        queue.add(postRequest)
        return list
    }



}




