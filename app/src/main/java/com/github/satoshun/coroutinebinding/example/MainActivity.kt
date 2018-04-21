package com.github.satoshun.coroutinebinding.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.satoshun.coroutinebinding.attaches
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    launch(UI) {
      for (attach in text.attaches()) {
        Log.d("attach", attach.toString())
      }
    }

    launch(UI) {
      while (true) {
        delay(2000, TimeUnit.MILLISECONDS)
        if (!text.isAttachedToWindow) {
          root.addView(text)
        } else {
          root.removeView(text)
        }
      }
    }
  }
}
