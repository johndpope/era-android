package com.josiassena.bluetooth.helpers

import java.util.*

const val EXTRAS_DEVICE_NAME = "DEVICE_NAME"
const val EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS"
const val BT_INIT_FAILED = "Unable to initialize Bluetooth"

// To enable the notification value
val ENABLE_NOTIFICATION_VALUE = byteArrayOf(0x01.toByte(), 0x00)

// To disable the notification value
val DISABLE_NOTIFICATION_VALUE = byteArrayOf(0x00.toByte(), 0x00)

// Client Characteristic UUID Values to set for notification.
var CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

// VSN Simple Service to listen the key press,fall detect and acknowledge and cancel the event.
val SERVICE_VSN_SIMPLE_SERVICE = UUID.fromString("fffffff0-00f7-4000-b000-000000000000")// 0xFFF0

val CHAR_DETECTION_CONFIG = UUID.fromString("fffffff2-00f7-4000-b000-000000000000")// 0xFFF2

// Characteristic UUID for acknowledge the data received and cancel the key press / fall detect event.
val ACK_DETECT = UUID.fromString("fffffff3-00f7-4000-b000-000000000000")// 0xFFF3

val CHAR_DETECTION_NOTIFY = UUID.fromString("fffffff4-00f7-4000-b000-000000000000")// 0xFFF4

//Characteristic UUID to secure the puck and restrict to respond to other APP.
val CHAR_APP_VERIFICATION = UUID.fromString("fffffff5-00f7-4000-b000-000000000000")//0xFFF5

// Value need to write the acknowledge data received.
val RECEIVED_ACK = byteArrayOf(0x01.toByte())

// Value need to write to cancel the key press / fall detect.
val CANCEL_ACK = byteArrayOf(0x00.toByte())

// New Value need to write within 30 seconds of connection
val NEW_APP_VERIFICATION_VALUE = byteArrayOf(0x80.toByte(), 0xBE.toByte(), 0xF5.toByte(), 0xAC.toByte(), 0xFF.toByte())

/**
 * ############################################################################################
 *
 *
 * Note: in order to enable more than one operation at a time, the values should be added
 * together. For example, if you want to enable fall detection and long press detection, the
 * value should be 0x06. If you take a look at the developer docs, long press = 0x02 and fall
 * detection = 0x04 so the total is 0x06;
 *
 *
 * ############################################################################################
 */

val ENABLE_KEY_DETECTION_VALUE = byteArrayOf(0x01.toByte())

val ENABLE_LONG_PRESS_DETECTION_VALUE = byteArrayOf(0x02.toByte())

val ENABLE_PRESS_RELEASE_LONG_PRESS_DETECTION_VALUE = byteArrayOf(0x03.toByte())

val ENABLE_FALL_DETECTION_VALUE = byteArrayOf(0x04.toByte())

val ENABLE_FALL_AND_LONG_PRESS_DETECTION_VALUE = byteArrayOf(0x06.toByte())