package com.rapidsos.era.splash_screen.view

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import com.rapidsos.era.*
import com.rapidsos.era.application.App
import com.rapidsos.era.helpers.*
import com.rapidsos.era.helpers.log_out.LogOutController
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.RequestsVerifier
import io.appflate.restmock.utils.RequestMatchers
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class AuthActivityTest : BaseAndroidTest() {

    private val oauthTokenMatcher = RequestMatchers.pathContains("oauth/token")
    private val registerMatcher = RequestMatchers.pathContains("v1/rain/user")

    private val app = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as TestApplication

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Rule
    @JvmField
    var testRule = MockWebServeRule(app)

    @Inject
    lateinit var logOutController: LogOutController

    companion object {
        private const val USERNAME = "fakeusername"
        private const val PASSWORD = "fakepassword"
        private const val EMAIL = "some_email@email.com"
    }

    @Before
    fun setUp() {
        app.host = RESTMockServer.getUrl()
        super.setup()

        component.inject(this)
        App.component = component

        logOutController.logOut()
    }

    @Test
    fun testBottomNavigation() {
        onView(allOf<View>(withId(R.id.navigation_register),
                childAtPosition(childAtPosition(withId(R.id.authBottomNavigation), 0), 1),
                isDisplayed())).perform(click())

        onView(withId(R.id.etRegisterUserName)).check(matches(isDisplayed()))
        onView(withId(R.id.etRegisterEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.etRegisterPassword)).check(matches(isDisplayed()))

        onView(allOf<View>(withId(R.id.navigation_login),
                childAtPosition(childAtPosition(withId(R.id.authBottomNavigation), 0), 0),
                isDisplayed())).perform(click())

        onView(withId(R.id.etLoginUsername)).check(matches(isDisplayed()))
        onView(withId(R.id.etLoginPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginSuccess() {
        RESTMockServer.whenPOST(oauthTokenMatcher)
                .thenReturnString(OK_SUCCESS_REQUEST_CODE, TEST_SUCCESS_AUTH_TOKEN_RESPONSE)

        onView(withId(R.id.etLoginUsername)).perform(typeText(USERNAME))
        onView(withId(R.id.etLoginPassword)).perform(typeText(PASSWORD))

        onView(withId(R.id.btnLogin)).perform(ViewActions.click())

        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("oauth/token"))

        onView(withId(R.id.tvInfo)).check(matches(isDisplayed()))

        onView(allOf(withId(R.id.btnProceed), withText(app.getString(R.string.confirm))))
                .check(matches(isDisplayed()))

        onView(allOf(withId(R.id.tvTermsAndPrivacy),
                withText(app.getString(R.string.terms_of_use_and_privacy_policy))))
                .check(matches(isDisplayed()))
    }

    @Test
    fun testLoginErrorInvalidCredentials401() {
        RESTMockServer.whenPOST(oauthTokenMatcher)
                .thenReturnString(UNAUTHORIZED_REQUEST_CODE, TEST_UNAUTHORIZED_RESPONSE)

        onView(withId(R.id.etLoginUsername)).perform(typeText(USERNAME))
        onView(withId(R.id.etLoginPassword)).perform(typeText(PASSWORD))

        onView(withId(R.id.btnLogin)).perform(ViewActions.click())

        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("oauth/token"))

        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText("Invalid user credentials.")))
                .check(matches(isDisplayed()))

        onView(withId(R.id.etLoginUsername)).check(matches(isDisplayed()))
        onView(withId(R.id.etLoginPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginErrorInvalidCredentials429() {
        RESTMockServer.whenPOST(oauthTokenMatcher)
                .thenReturnString(TOO_MANY_REQUEST_CODE, TEST_TOO_MANY_REQUESTS_RESPONSE)

        onView(withId(R.id.etLoginUsername)).perform(typeText(USERNAME))
        onView(withId(R.id.etLoginPassword)).perform(typeText(PASSWORD))

        onView(withId(R.id.btnLogin)).perform(ViewActions.click())

        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("oauth/token"))

        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText("Too many requests. Please try again later.")))
                .check(matches(isDisplayed()))

        onView(withId(R.id.etLoginUsername)).check(matches(isDisplayed()))
        onView(withId(R.id.etLoginPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginErrorInvalidCredentials500() {
        RESTMockServer.whenPOST(oauthTokenMatcher).thenReturnString(INTERNAL_SERVER_ERROR, "{}")

        onView(withId(R.id.etLoginUsername)).perform(typeText(USERNAME))
        onView(withId(R.id.etLoginPassword)).perform(typeText(PASSWORD))

        onView(withId(R.id.btnLogin)).perform(ViewActions.click())

        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("oauth/token"))

        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText("Something went wrong, please try again.")))
                .check(matches(isDisplayed()))

        onView(withId(R.id.etLoginUsername)).check(matches(isDisplayed()))
        onView(withId(R.id.etLoginPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun testRegisteringSuccess() {
        onView(allOf<View>(withId(R.id.navigation_register),
                childAtPosition(childAtPosition(withId(R.id.authBottomNavigation), 0), 1),
                isDisplayed())).perform(click())

        RESTMockServer.whenPOST(oauthTokenMatcher)
                .thenReturnString(OK_SUCCESS_REQUEST_CODE, TEST_SUCCESS_AUTH_TOKEN_RESPONSE)

        RESTMockServer.whenPOST(registerMatcher)
                .thenReturnString(OK_SUCCESS_REQUEST_CODE, TEST_SUCCESS_REGISTERING_RESPONSE)

        onView(withId(R.id.etRegisterUserName)).perform(typeText(USERNAME))
        onView(withId(R.id.etRegisterEmail)).perform(typeText(EMAIL))
        onView(withId(R.id.etRegisterPassword)).perform(typeText(PASSWORD))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.btnRegister)).perform(ViewActions.click())

        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("oauth/token"))
        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("v1/rain/user"))

        onView(withId(R.id.tvInfo)).check(matches(isDisplayed()))

        onView(allOf(withId(R.id.btnProceed), withText(app.getString(R.string.confirm))))
                .check(matches(isDisplayed()))

        onView(allOf(withId(R.id.tvTermsAndPrivacy),
                withText(app.getString(R.string.terms_of_use_and_privacy_policy))))
                .check(matches(isDisplayed()))
    }

    @Test
    fun testRegisteringErrorUserWithSameEmailOrUsernameExists400() {
        onView(allOf<View>(withId(R.id.navigation_register),
                childAtPosition(childAtPosition(withId(R.id.authBottomNavigation), 0), 1),
                isDisplayed())).perform(click())

        RESTMockServer.whenPOST(oauthTokenMatcher)
                .thenReturnString(OK_SUCCESS_REQUEST_CODE, TEST_SUCCESS_AUTH_TOKEN_RESPONSE)

        RESTMockServer.whenPOST(registerMatcher)
                .thenReturnString(BAD_REQUEST_CODE, TEST_REGISTRATION_ERROR_USER_EXISTS_RESPONSE)

        onView(withId(R.id.etRegisterUserName)).perform(typeText(USERNAME))
        onView(withId(R.id.etRegisterEmail)).perform(typeText(EMAIL))
        onView(withId(R.id.etRegisterPassword)).perform(typeText(PASSWORD))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.btnRegister)).perform(ViewActions.click())

        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("oauth/token"))
        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("v1/rain/user"))

        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText("User with the same username or email already exists")))
                .check(matches(isDisplayed()))

        onView(withId(R.id.etRegisterUserName)).check(matches(isDisplayed()))
        onView(withId(R.id.etRegisterEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.etRegisterPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun testRegisteringErrorBadRequest400() {
        onView(allOf<View>(withId(R.id.navigation_register),
                childAtPosition(childAtPosition(withId(R.id.authBottomNavigation), 0), 1),
                isDisplayed())).perform(click())

        RESTMockServer.whenPOST(oauthTokenMatcher)
                .thenReturnString(OK_SUCCESS_REQUEST_CODE, TEST_SUCCESS_AUTH_TOKEN_RESPONSE)

        RESTMockServer.whenPOST(registerMatcher)
                .thenReturnString(BAD_REQUEST_CODE, TEST_BAD_REQUEST_RESPONSE)

        onView(withId(R.id.etRegisterUserName)).perform(typeText(USERNAME))
        onView(withId(R.id.etRegisterEmail)).perform(typeText(EMAIL))
        onView(withId(R.id.etRegisterPassword)).perform(typeText(PASSWORD))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.btnRegister)).perform(ViewActions.click())

        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("oauth/token"))
        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("v1/rain/user"))

        Espresso.closeSoftKeyboard()
        Thread.sleep(1000)

        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText("Please try again later.")))
                .check(matches(isDisplayed()))

        onView(withId(R.id.etRegisterUserName)).check(matches(isDisplayed()))
        onView(withId(R.id.etRegisterEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.etRegisterPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun testRegisteringErrorUserWithSameEmailOrUsernameExists429() {
        onView(allOf<View>(withId(R.id.navigation_register),
                childAtPosition(childAtPosition(withId(R.id.authBottomNavigation), 0), 1),
                isDisplayed())).perform(click())

        RESTMockServer.whenPOST(oauthTokenMatcher)
                .thenReturnString(OK_SUCCESS_REQUEST_CODE, TEST_SUCCESS_AUTH_TOKEN_RESPONSE)

        RESTMockServer.whenPOST(registerMatcher)
                .thenReturnString(TOO_MANY_REQUEST_CODE, TEST_TOO_MANY_REQUESTS_RESPONSE)

        onView(withId(R.id.etRegisterUserName)).perform(typeText(USERNAME))
        onView(withId(R.id.etRegisterEmail)).perform(typeText(EMAIL))
        onView(withId(R.id.etRegisterPassword)).perform(typeText(PASSWORD))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.btnRegister)).perform(ViewActions.click())

        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("oauth/token"))
        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("v1/rain/user"))

        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText("Too many requests. Please try again later.")))
                .check(matches(isDisplayed()))

        onView(withId(R.id.etRegisterUserName)).check(matches(isDisplayed()))
        onView(withId(R.id.etRegisterEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.etRegisterPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun testForgotPasswordOptionIsAvailableFromMainLoginScreen() {
        onView(withId(R.id.tvForgotLoginDetails)).check(matches(isDisplayed()))
    }

    private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return (parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position))
            }
        }
    }
}
