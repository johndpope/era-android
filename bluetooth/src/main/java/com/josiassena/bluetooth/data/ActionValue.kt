package com.josiassena.bluetooth.data

/**
 * @author Josias Sena
 */
enum class ActionValue(val value: Int) {

    RELEASE(0), // Button release detected
    PRESS(1), // Button press detected
    LONG_PRESS(3), // Button press-release between 2-10 seconds
    FALL(4)
}
