package com.github.satoshun.coroutinebinding.view

import android.view.View

/**
 * A scroll-change event on a view
 */
data class ViewScrollChangeEvent(
  val view: View,
  val scrollX: Int,
  val scrollY: Int,
  val oldScrollX: Int,
  val oldScrollY: Int
)
