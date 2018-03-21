package com.josiassena.bluetooth.helpers

import com.josiassena.bluetooth.data.ActionValue


/**
 * @author Josias Sena
 */
object ActionValueMessage {

    private const val DETECTED_A_BUTTON_PRESS = "Detected a button press"
    private const val DETECTED_A_BUTTON_RELEASE = "Detected a button release"
    private const val DETECTED_A_LONG_PRESS = "Detected a long press"
    private const val DETECTED_A_FALL = "Detected a Fall"

    fun getMessageFromValue(actionValue: ByteArray?): String {
        when (actionValue?.get(0)?.toInt()) {
            ActionValue.PRESS.value -> return DETECTED_A_BUTTON_PRESS
            ActionValue.RELEASE.value -> return DETECTED_A_BUTTON_RELEASE
            ActionValue.LONG_PRESS.value -> return DETECTED_A_LONG_PRESS
            ActionValue.FALL.value -> return DETECTED_A_FALL
        }

        return ""
    }
}