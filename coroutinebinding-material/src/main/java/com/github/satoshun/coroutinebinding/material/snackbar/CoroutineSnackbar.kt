package com.github.satoshun.coroutinebinding.material.snackbar

import androidx.annotation.CheckResult
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel which emits the dismiss events from view.
 */
@CheckResult
fun Snackbar.dismisses(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel(capacity) {
  val listener = object : Snackbar.Callback() {
    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
      safeOffer(event)
    }
  }
  invokeOnCloseOnMain {
    removeCallback(listener)
  }
  addCallback(listener)
}

/**
 * Suspend a channel which emits the dismiss event from view.
 */
@CheckResult
suspend fun Snackbar.awaitDismiss(): Int = suspendCancellableCoroutine { cont ->
  val listener = object : Snackbar.Callback() {
    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
      cont.resume(event)
      removeCallback(this)
    }
  }
  cont.invokeOnCancellation {
    removeCallback(listener)
  }
  addCallback(listener)
}
