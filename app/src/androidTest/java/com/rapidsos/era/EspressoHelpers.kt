package com.rapidsos.era

import android.support.design.widget.TextInputLayout
import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * @author Josias Sena
 */
class EspressoHelpers {

    companion object {

        /**
         * Checks the error in an TextInputLayout
         */
        fun inputLayoutHasErrorText(expectedErrorText: String): Matcher<View> {
            return object : TypeSafeMatcher<View>() {

                override fun matchesSafely(view: View?): Boolean {
                    if (view !is TextInputLayout) {
                        return false
                    }

                    return expectedErrorText == view.error
                }

                override fun describeTo(description: Description?) {
                }
            }
        }
    }

}