package com.sh22.frontend_networking_test

import android.app.PendingIntent.getActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.unity3d.player.UnityPlayer


class MainActivity : AppCompatActivity() {
    var mUnityPlayer: UnityPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set a onClick callback for the test button
        val button = findViewById<Button>(R.id.test_button)
        button.setOnClickListener(View.OnClickListener {
            UnityPlayer.UnitySendMessage("HealthManager", "SetHealth", "1.0")
        });
    }

    override fun onDestroy() {
        mUnityPlayer?.quit()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        mUnityPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        mUnityPlayer?.resume()
    }
}