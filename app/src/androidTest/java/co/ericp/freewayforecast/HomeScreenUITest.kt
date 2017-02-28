package co.ericp.freewayforecast

import android.support.test.InstrumentationRegistry
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
import co.ericp.freewayforecast.routeForecast.RouteForecast
import co.ericp.freewayforecast.routes.Route
import io.reactivex.Observable
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.mockito.BDDMockito.*

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeScreenUITest {
    val forecastRule = RouteForecastRule(InstrumentationRegistry.getTargetContext())
    val activityRule = ActivityTestRule(MainActivity::class.java)

    // RouteForecastRule needs to go first to make sure the components are set
    // in the Application before any Activity is launched.
    @Rule
    val bothRules: TestRule = RuleChain.outerRule(forecastRule).around(activityRule)

    @Test
    fun displaysInputAtFirst() {
        given(forecastRule.routeForecastSource.getRouteForecasts(any(), anyLong()))
                .will { invocation ->
            val routes = invocation.arguments[0] as List<Route>
            Observable.empty<RouteForecast>()
        }

        onView(withId(R.id.input_origin)).check(matches(isDisplayed()))
    }
}
