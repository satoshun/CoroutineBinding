package com.github.satoshun.coroutinebinding.widget

import android.widget.CompoundButton
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel of booleans representing the checked state of CompoundButton.
 */
fun CompoundButton.checkedChanges(capacity: Int = 0): ReceiveChannel<Boolean> = cancelableChannel(capacity) {
  val listener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
    safeOffer(isChecked)
  }
  invokeOnCloseOnMain {
    setOnCheckedChangeListener(null)
  }
  setOnCheckedChangeListener(listener)
}

/**
 * Suspend a the checked state event of CompoundButton.
 */
suspend fun CompoundButton.awaitCheckedChange(): Boolean = suspendCancellableCoroutine { cont ->
  val listener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
    cont.resume(isChecked)
    setOnCheckedChangeListener(null)
  }
  cont.invokeOnCancellation {
    setOnCheckedChangeListener(null)
  }
  setOnCheckedChangeListener(listener)
}
