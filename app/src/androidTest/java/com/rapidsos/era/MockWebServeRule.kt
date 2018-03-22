package com.rapidsos.era

import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.RESTMockServerStarter
import io.appflate.restmock.android.AndroidAssetsFileParser
import io.appflate.restmock.android.AndroidLogger
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * A custom test rule for starting and stopping the mock web server during tests
 *
 * @author Josias Sena
 */
class MockWebServeRule(private val application: TestApplication) : TestRule {

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                // Start the mock server before every single Android test
                RESTMockServerStarter.startSync(AndroidAssetsFileParser(application), AndroidLogger())

                base?.evaluate()

                // Reset the server after every single Android test
                RESTMockServer.reset()
            }
        }
    }
}