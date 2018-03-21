package com.rapidsos.era.authentication.password_reset.view

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.rapidsos.era.*
import com.rapidsos.era.application.App
import com.rapidsos.era.authentication.auth_activity.view.AuthenticationActivity
import com.rapidsos.era.helpers.OK_SUCCESS_REQUEST_CODE
import com.rapidsos.era.helpers.log_out.LogOutController
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.RequestsVerifier
import io.appflate.restmock.utils.RequestMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * @author Josias Sena
 */
@RunWith(AndroidJUnit4::class)
class PasswordResetActivityTest : BaseAndroidTest() {

    private val oauthTokenMatcher = RequestMatchers.pathContains("oauth/token")
    private val resetPwdMatcher = RequestMatchers.pathContains("v1/rain/password-reset")

    private val app = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as TestApplication

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(AuthenticationActivity::class.java)

    @Rule
    @JvmField
    var testRule = MockWebServeRule(app)

    @Inject
    lateinit var logOutController: LogOutController

    companion object {
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
    fun testForgotPassword() {
        RESTMockServer.whenPOST(oauthTokenMatcher)
                .thenReturnString(OK_SUCCESS_REQUEST_CODE, TEST_SUCCESS_AUTH_TOKEN_RESPONSE)

        RESTMockServer.whenPOST(resetPwdMatcher).thenReturnString(OK_SUCCESS_REQUEST_CODE, "{}")

        onView(withId(R.id.tvForgotLoginDetails)).perform(click())

        onView(withId(R.id.tvForgotPwdTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.tvForgotPwdSubTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.tilPwdResetEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.btnResetPwd)).check(matches(isDisplayed()))

        onView(withId(R.id.etResetEmail)).perform(typeText(EMAIL))

        onView(withId(R.id.btnResetPwd)).perform(click())

        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("oauth/token"))
        RequestsVerifier.verifyPOST(RequestMatchers.pathEndsWith("v1/rain/password-reset"))

        onView(withId(R.id.etLoginUsername)).check(matches(isDisplayed()))
        onView(withId(R.id.etLoginPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))

        val message = app.getString(R.string.pwd_reset_email_sent)
        onView(withText(message))
                .inRoot(withDecorView(not(`is`(activityTestRule.activity.window.decorView))))
                .check(matches(isDisplayed()))
    }

    @Test
    fun testForgotPasswordWithEmptyPasswordField() {
        Espresso.onView(withId(R.id.tvForgotLoginDetails)).perform(click())

        Espresso.onView(withId(R.id.tvForgotPwdTitle)).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.tvForgotPwdSubTitle)).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.tilPwdResetEmail)).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.btnResetPwd)).perform(click())

        Espresso.onView(withId(R.id.tilPwdResetEmail))
                .check(matches(EspressoHelpers.inputLayoutHasErrorText("Cannot be empty")))
    }
}