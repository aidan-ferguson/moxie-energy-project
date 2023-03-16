package com.sh22.energy_saver_app

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sh22.energy_saver_app.ui.activites.LoginActivity
import com.sh22.energy_saver_app.ui.activites.MainActivity
import com.sh22.energy_saver_app.ui.fragments.HomeFragment
import com.sh22.energy_saver_app.ui.fragments.LoginFragment
import org.hamcrest.Matchers.not

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
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))

        //check nav bar can switch between fragments

        //check appliance page is displayed correctly
        onView(withId(R.id.appliances)).perform(click())
        onView(withId(R.id.appliance_recycler_view)).check(matches(isDisplayed()))

        //check ecosystem page is displayed correctly
        onView(withId(R.id.ecosystem)).perform(click())
        onView(withId(R.id.frameLayout3)).check(matches(isDisplayed()))

        //check home page is displayed correctly
        onView(withId(R.id.home)).perform(click())
        onView(withId(R.id.tip_square)).check(matches(isDisplayed()))
        onView(withId(R.id.breakdown)).check(matches(isDisplayed()))
        onView(withId(R.id.kilowattButton)).check(matches(isDisplayed()))
        onView(withId(R.id.halfGauge)).check(matches(isDisplayed()))


    }

    @Test
    fun TipPageTest(){
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.tip_square)).perform(click())
        Thread.sleep(2500)
        onView(withId(R.id.breakdown)).check(matches(not(isDisplayed())))
        onView(withId(R.id.kilowattButton)).check(matches(not(isDisplayed())))
    }

    @Test
    fun breakdownPageTest(){
        onView(withId(R.id.breakdown)).perform(click())
        Thread.sleep(2500)
        onView(withId(R.id.tip_square)).check(matches(not(isDisplayed())))
        onView(withId(R.id.kilowattButton)).check(matches(not(isDisplayed())))
    }

    @Test
    fun krewPageTest(){
        onView(withId(R.id.kilowattButton)).perform(click())
        Thread.sleep(2500)
        onView(withId(R.id.tip_square)).check(matches(not(isDisplayed())))
        onView(withId(R.id.breakdown)).check(matches(not(isDisplayed())))
    }
}
