package com.github.satoshun.coroutinebinding

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout

class ViewActivity : Activity() {
  lateinit var view: ViewGroup

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    view = FrameLayout(this).apply {
      layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
      )
    }
    setContentView(view)
  }
}
