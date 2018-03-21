package com.rapidsos.rain.auth

import com.google.gson.Gson
import com.rapidsos.rain.TEST_SUCCESS_AUTH_TOKEN_RESPONSE
import com.rapidsos.rain.TEST_TOO_MANY_REQUESTS_RESPONSE
import com.rapidsos.rain.TEST_UNAUTHORIZED_RESPONSE
import com.rapidsos.rain.api.ApiBuilder
import com.rapidsos.rain.api.RetrofitConfigurations
import com.rapidsos.rain.data.network_response.SessionToken
import com.rapidsos.rain.data.user.User
import com.rapidsos.rain.helpers.OK_SUCCESS_REQUEST_CODE
import com.rapidsos.rain.helpers.TOO_MANY_REQUEST_CODE
import com.rapidsos.rain.helpers.UNAUTHORIZED_REQUEST_CODE
import com.rapidsos.rain.rules.MockWebServerRule
import com.rapidsos.rain.rules.RxRule
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers
import io.reactivex.observers.TestObserver
import okhttp3.Cache
import okhttp3.mockwebserver.MockResponse
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

/**
 * @author Josias Sena
 */
class LoginControllerTest {

    private val matcher = RequestMatchers.pathContains("oauth/token")

    private lateinit var retrofitConfigurations: RetrofitConfigurations

    @Rule
    @JvmField
    var mockWebServerRule = MockWebServerRule()

    companion object {
        @ClassRule
        @JvmField
        var rxRule = RxRule()
    }

    @Before
    fun setUp() {
        val cache = Mockito.mock(Cache::class.java)
        retrofitConfigurations = RetrofitConfigurations(cache)
    }

    @Test
    fun testLoginSuccess200() {
        val mockResponse = MockResponse()
                .setBody(TEST_SUCCESS_AUTH_TOKEN_RESPONSE)
                .setResponseCode(OK_SUCCESS_REQUEST_CODE)

        RESTMockServer.whenPOST(matcher).thenReturn(mockResponse)

        val api = ApiBuilder(retrofitConfigurations).buildApi(RESTMockServer.getUrl())

        val user = User().apply {
            username = "testusername"
            email = "test@gmail.com"
            password = "testpwd"
            phone = "15555555555"
        }

        val testObserver = TestObserver.create<SessionToken>()
        val loginManager = LoginController(api)
        loginManager.login(user, testObserver)

        val sessionToken = Gson().fromJson(TEST_SUCCESS_AUTH_TOKEN_RESPONSE, SessionToken::class.java)

        with(testObserver) {
            assertNoErrors()
            awaitTerminalEvent()
            assertValue(sessionToken)
        }
    }

    @Test
    fun testLoginErrorInvalidCredentials401() {
        val mockResponse = MockResponse()
                .setBody(TEST_UNAUTHORIZED_RESPONSE)
                .setResponseCode(UNAUTHORIZED_REQUEST_CODE)

        RESTMockServer.whenPOST(matcher).thenReturn(mockResponse)

        val api = ApiBuilder(retrofitConfigurations).buildApi(RESTMockServer.getUrl())

        val user = User().apply {
            username = "testusername"
            email = "test@gmail.com"
            password = "testpwd"
            phone = "15555555555"
        }

        val testObserver = TestObserver.create<SessionToken>()
        val loginManager = LoginController(api)
        loginManager.login(user, testObserver)

        with(testObserver) {
            awaitTerminalEvent()
            assertErrorMessage("Invalid user credentials.")
            assertNoValues()
        }
    }

    @Test
    fun testLoginErrorTooManyRequests429() {
        val mockResponse = MockResponse()
                .setBody(TEST_TOO_MANY_REQUESTS_RESPONSE)
                .setResponseCode(TOO_MANY_REQUEST_CODE)

        RESTMockServer.whenPOST(matcher).thenReturn(mockResponse)

        val api = ApiBuilder(retrofitConfigurations).buildApi(RESTMockServer.getUrl())

        val user = User().apply {
            username = "testusername"
            email = "test@gmail.com"
            password = "testpwd"
            phone = "15555555555"
        }

        val testObserver = TestObserver.create<SessionToken>()
        val loginManager = LoginController(api)
        loginManager.login(user, testObserver)

        with(testObserver) {
            awaitTerminalEvent()
            assertErrorMessage("Too many requests. Please try again later.")
            assertNoValues()
        }
    }
}