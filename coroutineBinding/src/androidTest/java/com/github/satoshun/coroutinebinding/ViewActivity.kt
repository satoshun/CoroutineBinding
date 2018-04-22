package com.github.satoshun.coroutinebinding

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ViewActivity : Activity() {
  val rootView: ViewGroup get() = findViewById(android.R.id.content)
  lateinit var view: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    view = TextView(this)
    setContentView(view)
  }
}
