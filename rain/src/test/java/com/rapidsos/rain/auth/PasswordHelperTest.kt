package com.rapidsos.rain.auth

import com.rapidsos.rain.INVALID_TOKEN_SUCCESS_RESPONSE
import com.rapidsos.rain.RESET_PWD_SUCCESS_RESPONSE
import com.rapidsos.rain.RESET_PWD_UNAUTHORIZED_RESPONSE
import com.rapidsos.rain.TOKEN_SUCCESS_RESPONSE
import com.rapidsos.rain.api.ApiBuilder
import com.rapidsos.rain.api.RetrofitConfigurations
import com.rapidsos.rain.helpers.INTERNAL_SERVER_ERROR_CODE
import com.rapidsos.rain.helpers.OK_SUCCESS_REQUEST_CODE
import com.rapidsos.rain.helpers.UNAUTHORIZED_REQUEST_CODE
import com.rapidsos.rain.rules.MockWebServerRule
import com.rapidsos.rain.rules.RxRule
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers
import io.reactivex.observers.TestObserver
import okhttp3.Cache
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response

/**
 * @author Josias Sena
 */
class PasswordHelperTest {

    private val tokenMatcher = RequestMatchers.pathContains("oauth/token")
    private val resetPwdMatcher = RequestMatchers.pathContains("v1/rain/password-reset")

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
    fun resetPasswordGetAccessToken200AndResetPassword200() {
        val tokenMockResponse = MockResponse()
                .setBody(TOKEN_SUCCESS_RESPONSE)
                .setResponseCode(OK_SUCCESS_REQUEST_CODE)

        RESTMockServer.whenPOST(tokenMatcher).thenReturn(tokenMockResponse)

        val resetPwdMockResponse = MockResponse()
                .setBody(RESET_PWD_SUCCESS_RESPONSE)
                .setResponseCode(OK_SUCCESS_REQUEST_CODE)

        RESTMockServer.whenPOST(resetPwdMatcher).thenReturn(resetPwdMockResponse)

        val api = ApiBuilder(retrofitConfigurations).buildApi(RESTMockServer.getUrl())

        val testObserver = TestObserver.create<Response<ResponseBody>>()
        val passwordHelper = PasswordHelper(api)
        passwordHelper.resetPassword("abc@gmail.com", "Basic 123", testObserver)

        with(testObserver) {
            assertNoErrors()
            awaitTerminalEvent()
            assertComplete()
        }
    }

    @Test
    fun resetPasswordGetAccessToken200AndResetPassword401() {
        val tokenMockResponse = MockResponse()
                .setBody(INVALID_TOKEN_SUCCESS_RESPONSE)
                .setResponseCode(OK_SUCCESS_REQUEST_CODE)

        RESTMockServer.whenPOST(tokenMatcher).thenReturn(tokenMockResponse)

        val resetPwdMockResponse = MockResponse()
                .setBody(RESET_PWD_UNAUTHORIZED_RESPONSE)
                .setResponseCode(UNAUTHORIZED_REQUEST_CODE)

        RESTMockServer.whenPOST(resetPwdMatcher).thenReturn(resetPwdMockResponse)

        val api = ApiBuilder(retrofitConfigurations).buildApi(RESTMockServer.getUrl())

        val testObserver = TestObserver.create<Response<ResponseBody>>()
        val passwordHelper = PasswordHelper(api)
        passwordHelper.resetPassword("abc@gmail.com", "Basic 123", testObserver)

        with(testObserver) {
            awaitTerminalEvent()
            assertErrorMessage("Client Error")
            assertNoValues()
        }
    }

    @Test
    fun resetPasswordGetAccessTokenError() {
        RESTMockServer.whenPOST(tokenMatcher).thenReturnEmpty(INTERNAL_SERVER_ERROR_CODE)

        val api = ApiBuilder(retrofitConfigurations).buildApi(RESTMockServer.getUrl())

        val testObserver = TestObserver.create<Response<ResponseBody>>()
        val passwordHelper = PasswordHelper(api)
        passwordHelper.resetPassword("abc@gmail.com", "", testObserver)

        with(testObserver) {
            awaitTerminalEvent()
            assertNotComplete()
            assertNoValues()
        }
    }

}