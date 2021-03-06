package com.github.satoshun.coroutinebinding.widget

import android.widget.RadioGroup
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Create an channel which emits the checked events.
 */
fun RadioGroup.checkedChanges(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel(capacity) {
  val listener = object : RadioGroup.OnCheckedChangeListener {
    var lastChecked = -1

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
      if (lastChecked != checkedId) {
        lastChecked = checkedId
        safeOffer(checkedId)
      }
    }
  }
  invokeOnCloseOnMain {
    setOnCheckedChangeListener(null)
  }
  setOnCheckedChangeListener(listener)
}

/**
 * Suspend a which emits the checked event.
 */
suspend fun RadioGroup.awaitCheckedChange(): Int = suspendCancellableCoroutine { cont ->
  val listener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
    cont.resume(checkedId)
    setOnCheckedChangeListener(null)
  }
  cont.invokeOnCancellation {
    setOnCheckedChangeListener(null)
  }
  setOnCheckedChangeListener(listener)
}
