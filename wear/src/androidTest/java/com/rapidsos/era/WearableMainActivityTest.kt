package com.rapidsos.era

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Josias Sena
 */
@RunWith(AndroidJUnit4::class)
class WearableMainActivityTest {

    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.WAKE_LOCK)

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(WearableMainActivity::class.java)

    @Test
    fun testPanicSuccess() {
        onView(withId(R.id.btnPanic)).check(matches(isDisplayed()))
        onView(withId(R.id.btnPanic)).perform(click())

        // Verify the confirmation dialog is displayed
        onView(withText(R.string.confirmation)).check(matches(isDisplayed()))
        onView(withText(R.string.confirm_calling_911)).check(matches(isDisplayed()))
        onView(withText(R.string.yes)).check(matches(isDisplayed()))
        onView(withText(R.string.no)).check(matches(isDisplayed()))

        // Initiate panic sequence
        onView(withText(R.string.yes)).perform(click())

        // Verify we are no longer in the activity
        Assert.assertTrue(activityTestRule.activity.isDestroyed)
    }

    @Test
    fun testPanicCancellation() {
        onView(withId(R.id.btnPanic)).check(matches(isDisplayed()))
        onView(withId(R.id.btnPanic)).perform(click())

        // Verify the confirmation dialog is displayed
        onView(withText(R.string.confirmation)).check(matches(isDisplayed()))
        onView(withText(R.string.confirm_calling_911)).check(matches(isDisplayed()))
        onView(withText(R.string.yes)).check(matches(isDisplayed()))
        onView(withText(R.string.no)).check(matches(isDisplayed()))

        // Initiate panic sequence
        onView(withText(R.string.no)).perform(click())

        // Verify we are no longer in the activity
        Assert.assertFalse(activityTestRule.activity.isDestroyed)
        onView(withId(R.id.btnPanic)).check(matches(isDisplayed()))
    }
}