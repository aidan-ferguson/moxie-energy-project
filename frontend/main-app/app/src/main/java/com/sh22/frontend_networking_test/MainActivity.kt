package com.sh22.frontend_networking_test

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set a onClick callback for the test button
        val button = findViewById<Button>(R.id.ecosystem_button)
        button.setOnClickListener(View.OnClickListener {
            val myIntent = Intent(this, EcosystemActivity::class.java)
            startActivity(myIntent)
        })
    }
}