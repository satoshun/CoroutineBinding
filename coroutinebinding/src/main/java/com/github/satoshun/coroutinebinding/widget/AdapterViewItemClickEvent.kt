package com.github.satoshun.coroutinebinding.widget

import android.view.View
import android.widget.AdapterView

/**
 * A adapter-view click event.
 */
data class AdapterViewItemClickEvent(
    val view: AdapterView<*>,
    val clickedView: View,
    val position: Int,
    val id: Long
)
