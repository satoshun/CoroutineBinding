package com.github.satoshun.coroutinebinding

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout

class ViewActivity : Activity() {
  val rootView: ViewGroup get() = findViewById(android.R.id.content)
  lateinit var view: ViewGroup

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    view = FrameLayout(this)
    setContentView(view)
  }
}
