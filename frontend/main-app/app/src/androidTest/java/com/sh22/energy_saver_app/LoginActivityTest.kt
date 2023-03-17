package com.sh22.energy_saver_app

import android.content.Context
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.sh22.energy_saver_app.ui.activites.LoginActivity
import org.hamcrest.CoreMatchers.anything
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)

class LoginActivityTest {
    @Before
    fun clear_local_token() {
        TestUtilities.clear_token(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @Test
    fun logInTest(){
        ActivityScenario.launch(LoginActivity::class.java)

        Thread.sleep(2000);
        onView(withId(R.id.username)).perform(click()).perform(typeText("admin"))
        onView(withId(R.id.password)).perform(click()).perform(typeText("password"), closeSoftKeyboard())
        Thread.sleep(1000);
        onView(withId(R.id.login_button)).perform(click())

        Thread.sleep(2000)
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))
    }

    @Test
    fun RegisterNewUserTest(){
        ActivityScenario.launch(LoginActivity::class.java)

        //register new user with fake details
        Thread.sleep(2000);
        onView(withId(R.id.login_register_button)).perform(click())
        Thread.sleep(1500)

        onView(withId(R.id.register_username)).perform(click()).perform(typeText("testuser@gmail.com"), closeSoftKeyboard())

        onView(withId(R.id.register_password)).perform(click()).perform(typeText("test1234"), closeSoftKeyboard())

        onView(withId(R.id.register_password_confirm)).perform(click()).perform(typeText("test1234"), closeSoftKeyboard())

        onView(withId(R.id.register_firstname)).perform(click()).perform(typeText("test"), closeSoftKeyboard())

        onView(withId(R.id.register_surname)).perform(click()).perform(typeText("user"), closeSoftKeyboard())

        onView(withId(R.id.register_button)).perform(click())
        Thread.sleep(4000)

        //check homepage is displayed correctly after account creation
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))
        onView(withId(R.id.tip_square)).check(matches(isDisplayed()))
        onView(withId(R.id.breakdown)).check(matches(isDisplayed()))
        onView(withId(R.id.kilowattButton)).check(matches(isDisplayed()))
        onView(withId(R.id.halfGauge)).check(matches(isDisplayed()))

        //delete fake account
        onView(withId(R.id.settings_action_bar)).perform(click())
        onView(withText("Your Account")).perform(click())
        Thread.sleep(2000);
        onView(withId(R.id.delete_account_button)).perform(click())

        Thread.sleep(2000);
        //check account deletion was successful
        onView(withId(R.id.login_frame_layout)).check(matches(isDisplayed()))
    }
}