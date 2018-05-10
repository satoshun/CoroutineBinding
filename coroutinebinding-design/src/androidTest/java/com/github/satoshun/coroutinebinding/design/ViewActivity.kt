package com.github.satoshun.coroutinebinding.design

import android.app.Activity
import android.os.Bundle
import android.support.v7.view.ContextThemeWrapper
import android.view.ViewGroup
import android.widget.FrameLayout

class ViewActivity : Activity() {
  lateinit var view: ViewGroup

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val context = ContextThemeWrapper(this, R.style.Theme_AppCompat)
    view = FrameLayout(context).apply {
      layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
      )
    }
    setContentView(view)
  }
}
