package com.sh22.energy_saver_app

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sh22.energy_saver_app.ui.activites.MainActivity
import com.sh22.energy_saver_app.ui.fragments.HomeFragment
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
    fun NavBarTest() {
        // launches main activity and checks whether nav bar is in view
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        val fragmentScenario = launchFragmentInContainer<HomeFragment>()
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))

        //check nav bar can switch between fragments

        //check appliance page is displayed correctly
        onView(withId(R.id.appliances)).perform(click())
        onView(withId(R.id.appliance_heading)).check(matches(isDisplayed()))
        onView(withId(R.id.appliance_usage_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.appliance_recycler_view)).check(matches(isDisplayed()))

        //check ecosystem page is displayed correctly
        onView(withId(R.id.ecosystem)).perform(click())
        onView(withId(R.id.ecosystem_header2)).check(matches(isDisplayed()))

        //check home page is displayed correctly
        onView(withId(R.id.home)).perform(click())
        onView(withId(R.id.home_fragment_heading)).check(matches(isDisplayed()))
        onView(withId(R.id.home_fragment_heading)).check(matches(isDisplayed()))


    }
    @Test
    fun LogOutButtonTest(){
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        val scenario = launchFragmentInContainer<SettingsFragment>()
        onView(withId(R.id.settings_logout_button)).perform(click())

        onView(withId(R.id.login_heading)).check(matches(isDisplayed()))

    }
}