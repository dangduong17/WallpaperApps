package pion.tech.pionbase.flow

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pion.tech.pionbase.R
import pion.tech.pionbase.app.MainActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class NavigationFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testHomeToSettingFlow() {
        onView(withId(R.id.btnProfile)).perform(click())
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun testHomeToSearchFlow() {
        onView(withId(R.id.btnSearch)).perform(click())
        onView(withId(R.id.edtSearch)).check(matches(isDisplayed()))
    }
}
