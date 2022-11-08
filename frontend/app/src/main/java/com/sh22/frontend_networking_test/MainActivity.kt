package com.sh22.frontend_networking_test

import android.annotation.SuppressLint
import android.graphics.LightingColorFilter
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import com.sh22.frontend_networking_test.R

class MainActivity : AppCompatActivity() {
    private val progress = 40
    var score = progress.toFloat() / 100
    var progressBar: ProgressBar? = null
    var textView: TextView? = null
    var b1: Button? = null

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById<View>(R.id.progress_bar) as ProgressBar
        textView = findViewById<View>(R.id.text_view_progress) as TextView
        val type = Typeface.createFromAsset(assets, "fonts/FjallaOne-Regular.ttf")
        textView!!.setTypeface(type)
        progressBar!!.progress = progress
        textView!!.text = score.toString()
        val resultColor = ColorUtils.blendARGB(0xFF0000, 0x00FF00, score)
        textView!!.background.colorFilter = LightingColorFilter(resultColor, resultColor)
        textView!!.text = progress.toString()
    }
}