package com.rapidsos.utils.extensions

import android.support.annotation.DrawableRes
import android.widget.ImageView
import com.squareup.picasso.Picasso

/**
 * Load an image from a url into the ImageView
 *
 * @param url the url to load the image from
 */
fun ImageView.setImageFromUrl(@DrawableRes placeHolder: Int, url: String) =
        Picasso.with(context)
                .load(url)
                .error(placeHolder)
                .placeholder(placeHolder)
                .into(this)