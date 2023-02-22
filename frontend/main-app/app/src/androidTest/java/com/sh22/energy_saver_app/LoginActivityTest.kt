package com.sh22.energy_saver_app

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sh22.energy_saver_app.ui.activites.LoginActivity
import com.sh22.energy_saver_app.ui.activites.MainActivity
import com.sh22.energy_saver_app.ui.fragments.LoginFragment
import com.sh22.energy_saver_app.ui.fragments.SettingsFragment
import okhttp3.internal.wait

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
        val fragmentScenario = launchFragmentInContainer<LoginFragment>()

        onView(withId(R.id.username)).perform(click()).perform(typeText("admin"))

        onView(withId(R.id.password)).perform(click()).perform(typeText("password"))

        onView(withId(R.id.imageView2)).perform(click())

        onView(withId(R.id.login_button)).perform(click())

        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))
    }

    @Test
    fun RegisterNewUserTest(){
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        val fragmentScenario = launchFragmentInContainer<LoginFragment>()


        onView(withId(R.id.register_button)).perform(click())


    }
}