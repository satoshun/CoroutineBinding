package com.github.satoshun.coroutinebinding.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.satoshun.coroutinebinding.view.attaches
import com.github.satoshun.coroutinebinding.view.detaches
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
      for (attach in hello.attaches()) {
        Log.d("attach", attach.toString())
      }
    }

    launch(UI) {
      for (detach in hello.detaches()) {
        Log.d("detach", detach.toString())
      }
    }

    launch(UI) {
      while (true) {
        delay(5000, TimeUnit.MILLISECONDS)
        try {
          root.addView(hello)
        } catch (e: Exception) {
          root.removeView(hello)
        }
      }
    }
  }
}
