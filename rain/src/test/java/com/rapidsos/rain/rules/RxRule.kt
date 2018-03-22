package com.rapidsos.rain.rules

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * @author Josias Sena
 */
class RxRule : TestRule {

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

                base?.evaluate()

                RxAndroidPlugins.reset()
            }
        }
    }

}