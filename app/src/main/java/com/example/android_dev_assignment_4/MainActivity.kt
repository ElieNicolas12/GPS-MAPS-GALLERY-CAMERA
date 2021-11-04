package com.example.android_dev_assignment_4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ImageWay=findViewById<Button>(R.id.Image_way)
        ImageWay.setOnClickListener()
        {
            val intent=Intent(this, ImageActivity::class.java)
            startActivity(intent)
        }
       val locationWay=findViewById<Button>(R.id.Location_way)
        locationWay.setOnClickListener()
       {
            val intent2=Intent(this, GpsActivity::class.java)
            startActivity(intent2)
        }

    }
}