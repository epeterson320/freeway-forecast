package co.ericp.freewayforecast

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeScreenUITest {

    @Rule
    var mActivityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun displaysInputAtFirst() {
        onView(withId(R.id.input_origin)).check(matches(isDisplayed()))
    }
}
