package com.sh22.energy_saver_app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sh22.energy_saver_app.ui.activites.LoginActivity
import com.sh22.energy_saver_app.ui.activites.MainActivity
import com.sh22.energy_saver_app.ui.fragments.SettingsFragment

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)

class LoginActivityTest {
    @Test
    fun logInTest(){
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        onView(withId(R.id.username)).perform(typeText("admin"))
        onView(withId(R.id.password)).perform(typeText("password"))
        onView(withId(R.id.login_button)).perform(click())
    }
}