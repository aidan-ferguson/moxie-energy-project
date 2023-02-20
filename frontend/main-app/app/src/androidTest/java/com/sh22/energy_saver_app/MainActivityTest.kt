package com.sh22.energy_saver_app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
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
class MainActivityTest {
    @Test
    fun LogoutButtonTest(){
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.settings_logout_button)).perform(click())

    }

    fun NavBarTest() {
        // launches main activity and checks whether nav bar is in view
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))
    }

//    fun LogOutButtonTest(){
//        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
//        val fragmentScenario = FragmentScenario.launch(SettingsFragment::class.java)
//    }
}