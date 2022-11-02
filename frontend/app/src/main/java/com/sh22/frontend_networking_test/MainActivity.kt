package com.sh22.frontend_networking_test

import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set a onClick callback for the test button
        val button = findViewById<Button>(R.id.test_button)
        button.setOnClickListener(View.OnClickListener {
            // Because we can't run network operations on the main thread (it is used to render UI)
            //   we need to call the function in a co-routine
            GlobalScope.launch {
                val response = testAPICall()
                // Only the UI thread can access the views
                runOnUiThread {
                    val text = findViewById<TextView>(R.id.api_sample_text)
                    text.text = response
                }
            }
        });
    }

    // Function to call the API and return the response
    private fun testAPICall() : String {
        // Create URL
        // The IP 10.0.2.2 refers to the host computer
        val apiURL = URL("http://10.0.2.2:8000/api/users/1234/usage/electricity")

        // Create HTTP connection (we don't have SSL setup on django)
        val connection = apiURL.openConnection()
        if(connection is HttpURLConnection){
            // Check if connection was successful and return the response
            // connection.setRequestProperty("User-Agent", "android-test-app")
            if (connection.responseCode == 200) {
                // While we have new content, read it into a string
                val responseReader = InputStreamReader(connection.inputStream)
                val jsonReader = JsonReader(responseReader)

                // Just for example return the date and reading of the first entry
                jsonReader.beginObject()
                var returnStr = ""
                if(jsonReader.hasNext()) {
                    val key = jsonReader.nextName()
                    if(key.equals("success") && jsonReader.nextBoolean()) {
                        val usage = jsonReader.nextName()
                        if(usage.equals("usage")) {
                            // Enter array
                            jsonReader.beginArray()
                            while (jsonReader.hasNext()) {
                                // Enter the dictionary
                                jsonReader.beginObject()

                                // Get the timestamp and usage of the first element
                                jsonReader.nextName()
                                returnStr += jsonReader.nextString() + " - "
                                jsonReader.nextName()
                                returnStr += jsonReader.nextString() + "\n"

                                jsonReader.endObject()
                            }
                        }
                    }
                }

                connection.disconnect()
                responseReader.close()
                jsonReader.close()

                return returnStr
            } else {
                Log.e("testAPICall", "The server returned code " + connection.responseCode)
            }
        } else {
            Log.e("testAPICall", "Failed to create HttpConnection")
        }

        // If we reach this case, the function failed
        return "Error"
    }
}