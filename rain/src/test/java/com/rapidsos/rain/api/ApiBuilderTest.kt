package com.rapidsos.rain.api

import org.junit.Test
import org.mockito.Mockito
import retrofit2.Retrofit

/**
 * @author Josias Sena
 */
class ApiBuilderTest {

    @Test
    fun testBuildApiWorksAsExpected() {
        val configurations = Mockito.mock(RetrofitConfigurations::class.java)

        val mockInstance = Mockito.mock(Retrofit::class.java)
        Mockito.`when`(configurations.getRetrofitInstance(Mockito.anyString()))
                .thenReturn(mockInstance)

        val api = Mockito.mock(RainApi::class.java)
        Mockito.`when`(mockInstance.create(Mockito.any<Class<RainApi>>())).thenReturn(api)

        val apiBuilder = ApiBuilder(configurations)
        apiBuilder.buildApi("http://localhost")

        Mockito.verify(configurations).getRetrofitInstance("http://localhost")
        Mockito.verify(mockInstance).create(Mockito.any<Class<RainApi>>())
    }

}