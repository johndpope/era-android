package com.rapidsos.utils.extensions

import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

/**
 * Show the current view
 */
fun View.show() {
    this.visibility = View.VISIBLE
}

/**
 * Hide the current view
 */
fun View.hide() {
    this.visibility = View.GONE
}

/**
 * Show the current view
 */
fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}


/**
 * Get the current [TextView] text as a string instead of a [CharSequence]
 */
fun TextView.getString() = this.text.toString()

/**
 * Clears all text
 */
fun TextView.clearText() {
    this.text = ""
}

fun View.snack(message: String?, length: Int = Snackbar.LENGTH_LONG) {
    val snack = Snackbar.make(this, message.toString(), length)
    snack.show()
}

/**
 * Hides the soft keyboard
 */
fun View.hideKeyboard() {
    val inputMethodManager = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}