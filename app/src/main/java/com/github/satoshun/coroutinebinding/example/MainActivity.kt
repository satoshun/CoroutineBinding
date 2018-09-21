package com.github.satoshun.coroutinebinding.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.satoshun.coroutinebinding.view.attaches
import com.github.satoshun.coroutinebinding.view.awaitAttach
import com.github.satoshun.coroutinebinding.view.detaches
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

class MainActivity : AppCompatActivity(),
    CoroutineScope {

  private lateinit var job: Job
  override val coroutineContext: CoroutineContext get() = job + Dispatchers.Main

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    job = Job()

    launch {
      for (attach in hello.attaches()) {
        Log.d("attach", attach.toString())
      }
    }

    launch {
      for (detach in hello.detaches()) {
        Log.d("detach", detach.toString())
      }
    }

    launch {
      while (true) {
        delay(5000, TimeUnit.MILLISECONDS)
        try {
          root.addView(hello)
        } catch (e: Exception) {
          root.removeView(hello)
        }
      }
    }

    launch {
      while (true) {
        hello.awaitAttach()
        Log.d("suspend attach", "attached")
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    job.cancel()
  }
}
