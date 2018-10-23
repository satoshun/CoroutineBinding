package com.github.satoshun.coroutinebinding.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.satoshun.coroutinebinding.view.attaches
import com.github.satoshun.coroutinebinding.view.awaitAttach
import com.github.satoshun.coroutinebinding.view.awaitClick
import com.github.satoshun.coroutinebinding.view.awaitDetach
import com.github.satoshun.coroutinebinding.view.detaches
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

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
        delay(5000)
        try {
          root.addView(hello)
        } catch (e: Exception) {
          root.removeView(hello)
        }
      }
    }

    launch {
      val attach = async {
        while (true) {
          hello.awaitAttach()
          Log.d("suspend attach", "attached")
        }
      }
      val detach = async {
        while (true) {
          hello.awaitDetach()
          Log.d("suspend detach", "detached")
        }
      }
    }

    launch {
      button.awaitClick()
      Toast.makeText(this@MainActivity, "one shot clicked", Toast.LENGTH_LONG).show()
    }

    launch {
      var i = 1
      while (true) {
        button2.awaitClick()
        Toast.makeText(this@MainActivity, "clicked $i", Toast.LENGTH_SHORT).show()
        delay(300)
        i += 1
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    job.cancel()
  }
}
