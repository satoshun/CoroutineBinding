package com.github.satoshun.coroutinebinding.design.widget

import android.support.annotation.CheckResult
import android.support.design.widget.Snackbar
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the dismiss events from view.
 */
@CheckResult
fun Snackbar.dismisses(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = object : Snackbar.Callback() {
    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
      safeOffer(event)
    }
  }
  onAfterClosed {
    removeCallback(listener)
  }
  addCallback(listener)
}
