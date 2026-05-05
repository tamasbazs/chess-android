package io.bitrise.chess

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import io.bitrise.chess.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun chessAppLaunches() {
        onView(withId(R.id.chessBoardView)).check(matches(isDisplayed()))
    }

    @Test
    fun statusTextIsDisplayed() {
        onView(withId(R.id.statusText)).check(matches(isDisplayed()))
    }

    @Test
    fun resetButtonIsDisplayed() {
        onView(withId(R.id.resetButton)).check(matches(isDisplayed()))
    }
}
