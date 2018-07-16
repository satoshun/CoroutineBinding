package com.github.satoshun.coroutinebinding.constraintlayout

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout

class ViewActivity : Activity() {
  lateinit var view: ConstraintLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val context = ContextThemeWrapper(this, R.style.Theme_AppCompat)
    view = ConstraintLayout(context).apply {
      layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
      )
    }
    view.addView(TextView(this))
    view.addView(TextView(this))
    view.addView(TextView(this))

    view.loadLayoutDescription(R.xml.test_main)

    setContentView(view)
  }
}
