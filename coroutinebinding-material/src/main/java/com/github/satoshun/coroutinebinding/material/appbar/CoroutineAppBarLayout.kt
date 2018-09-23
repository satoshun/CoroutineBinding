package com.github.satoshun.coroutinebinding.material.appbar

import androidx.annotation.CheckResult
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel which emits the offset change in view.
 */
@CheckResult
fun AppBarLayout.offsetChanges(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel(capacity) {
  val listener = AppBarLayout.OnOffsetChangedListener { _, offset ->
    safeOffer(offset)
  }
  invokeOnCloseOnMain {
    removeOnOffsetChangedListener(listener)
  }
  addOnOffsetChangedListener(listener)
}

/**
 * Suspend a which emits the offset change in view.
 */
suspend fun AppBarLayout.awaitOffsetChange(): Int = suspendCancellableCoroutine { cont ->
  val listener = object : AppBarLayout.OnOffsetChangedListener {
    override fun onOffsetChanged(p0: AppBarLayout?, offset: Int) {
      cont.resume(offset)
      removeOnOffsetChangedListener(this)
    }
  }
  cont.invokeOnCancellation {
    removeOnOffsetChangedListener(listener)
  }
  addOnOffsetChangedListener(listener)
}
