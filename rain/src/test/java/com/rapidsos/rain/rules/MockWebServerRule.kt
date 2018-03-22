package com.rapidsos.rain.rules

import io.appflate.restmock.JVMFileParser
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.RESTMockServerStarter
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * @author Josias Sena
 */
class MockWebServerRule : TestRule {

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                RESTMockServerStarter.startSync(JVMFileParser())

                base?.evaluate()

                RESTMockServer.reset()
            }
        }
    }

}