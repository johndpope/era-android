package com.rapidsos.era

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner

/**
 * @author Josias Sena
 */
class EraTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?):
            Application = super.newApplication(cl, TestApplication::class.java.name, context)
}