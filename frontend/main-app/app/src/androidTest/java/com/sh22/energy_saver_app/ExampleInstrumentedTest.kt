package com.sh22.energy_saver_app

import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sh22.energy_saver_app.ui.activites.LoginActivity

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        ActivityScenario.launch(LoginActivity::class.java)
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.sh22.energy_saver_app", appContext.packageName)
    }
}