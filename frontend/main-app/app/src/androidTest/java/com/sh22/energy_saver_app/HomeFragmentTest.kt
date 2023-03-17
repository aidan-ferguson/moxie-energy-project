package com.sh22.energy_saver_app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
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

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {
    @Before
    fun login() {
        TestUtilities.clear_token(InstrumentationRegistry.getInstrumentation().getTargetContext());
        ActivityScenario.launch(LoginActivity::class.java)
        Thread.sleep(2000);

        onView(withId(R.id.username)).perform(click()).perform(ViewActions.typeText("admin"))
        onView(withId(R.id.password)).perform(click()).perform(ViewActions.typeText("password"), ViewActions.closeSoftKeyboard())
        Thread.sleep(1000);
        onView(withId(R.id.login_button)).perform(click())
        Thread.sleep(2000);
    }

    @Test
    fun NavBarTest() {
        // launches main activity and checks whether nav bar is in view
        ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(2000);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))

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
    fun krewPageTest(){
        Thread.sleep(2000);
        onView(withId(R.id.kilowattButton)).perform(click())
        Thread.sleep(2500)
        onView(withId(R.id.tip_square)).check(matches(not(isDisplayed())))
        onView(withId(R.id.breakdown)).check(matches(not(isDisplayed())))
        onView(withId(R.id.manage)).perform(click())
        onView(withId(R.id.friend_id)).check(matches(isDisplayed()));
//        onView(withId(R.id.friend_id)).perform(click()).perform(typeText(""))
    }

    @Test
    fun tipPageTest(){
        Thread.sleep(2000);
        onView(withId(R.id.tip_square)).perform(click())
        Thread.sleep(2500)
        onView(withId(R.id.breakdown)).check(matches(not(isDisplayed())))
        onView(withId(R.id.kilowattButton)).check(matches(not(isDisplayed())))
    }

    @Test
    fun breakdownPageTest(){
        Thread.sleep(2000);
        onView(withId(R.id.breakdown)).perform(click())
        Thread.sleep(2500)
        onView(withId(R.id.tip_square)).check(matches(not(isDisplayed())))
        onView(withId(R.id.kilowattButton)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testDataProviderChange() {
        Thread.sleep(2000);
        onView(withId(R.id.settings_action_bar)).perform(click())
        onView(ViewMatchers.withText("Your Account")).perform(click())

        onView(withId(R.id.data_provider_spinner)).perform(click())
        onData(allOf((instanceOf(String::class.java)), `is`("DALE:house_3"))).perform(click())
        onView(withId(R.id.data_provider_spinner)).perform(click())
        onData(allOf((instanceOf(String::class.java)), `is`("DALE:house_4"))).perform(click())
    }
}