package com.github.satoshun.coroutinebinding.androidx.swiperefreshlayout.widget

import androidx.annotation.CheckResult
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel of refresh events on view.
 */
@CheckResult
fun SwipeRefreshLayout.refreshes(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = SwipeRefreshLayout.OnRefreshListener {
    safeOffer(Unit)
  }
  invokeOnCloseOnMain {
    setOnRefreshListener(null)
  }
  setOnRefreshListener(listener)
}

/**
 * Suspend a of refresh event on view.
 */
suspend fun SwipeRefreshLayout.awaitRefresh(): Unit = suspendCancellableCoroutine { cont ->
  val listener = SwipeRefreshLayout.OnRefreshListener {
    cont.resume(Unit)
    setOnRefreshListener(null)
  }
  cont.invokeOnCancellation {
    setOnRefreshListener(null)
  }
  setOnRefreshListener(listener)
}
