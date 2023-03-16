package com.sh22.energy_saver_app

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
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

        onView(withId(R.id.username)).perform(click()).perform(typeText("admin"))

        onView(withId(R.id.password)).perform(click()).perform(typeText("password"), closeSoftKeyboard())

        Thread.sleep(1500)

        onView(withId(R.id.login_button)).perform(click())

        Thread.sleep(1500)

        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))
    }

    @Test
    fun RegisterNewUserTest(){
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        val fragmentScenario = FragmentScenario.launchInContainer(LoginFragment::class.java)

        //register new user with fake details
        onView(withId(R.id.login_register_button)).perform(click())
        Thread.sleep(1500)

        onView(withId(R.id.register_username)).perform(click())
        onView(withId(R.id.password)).perform(click()).perform(typeText("testuser@gmail.com"), closeSoftKeyboard())

        onView(withId(R.id.register_password)).perform(click())
        onView(withId(R.id.password)).perform(click()).perform(typeText("test1234"), closeSoftKeyboard())

        onView(withId(R.id.register_password_confirm)).perform(click())
        onView(withId(R.id.password)).perform(click()).perform(typeText("test1234"), closeSoftKeyboard())

        onView(withId(R.id.register_firstname)).perform(click())
        onView(withId(R.id.password)).perform(click()).perform(typeText("test"), closeSoftKeyboard())

        onView(withId(R.id.register_surname)).perform(click())
        onView(withId(R.id.password)).perform(click()).perform(typeText("user"), closeSoftKeyboard())

        onView(withId(R.id.register_button)).perform(click())
        Thread.sleep(1500)

        //check homepage is displayed correctly after account creation
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))
        onView(withId(R.id.tip_square)).check(matches(isDisplayed()))
        onView(withId(R.id.breakdown)).check(matches(isDisplayed()))
        onView(withId(R.id.kilowattButton)).check(matches(isDisplayed()))
        onView(withId(R.id.halfGauge)).check(matches(isDisplayed()))

        //delete fake account
        onView(withId(R.id.action_settings)).perform(click())
        onView(withId(R.id.delete_account_button)).perform(click())

        //check account deletion was successful
        onView(withId(R.id.login_frame_layout)).check(matches(isDisplayed()))
    }
}