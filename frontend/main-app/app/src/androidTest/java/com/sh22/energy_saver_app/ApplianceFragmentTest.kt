package com.sh22.energy_saver_app

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sh22.energy_saver_app.ui.activites.LoginActivity
import com.sh22.energy_saver_app.ui.activites.MainActivity
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

class ApplianceFragmentTest {
    @Before
    fun login() {
        TestUtilities.clear_token(InstrumentationRegistry.getInstrumentation().getTargetContext());
        ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.username)).perform(click()).perform(typeText("admin"))
        onView(withId(R.id.password)).perform(click()).perform(typeText("password"), ViewActions.closeSoftKeyboard())
        Thread.sleep(100);
        onView(withId(R.id.login_button)).perform(click())

        Thread.sleep(400);

        onView(withId(R.id.appliances)).perform(click())

        // Appliances can take a while to populate
        Thread.sleep(2000);
    }

    @Test
    fun checkGraphExists() {
        onView(withId(R.id.appliance_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

    }
}