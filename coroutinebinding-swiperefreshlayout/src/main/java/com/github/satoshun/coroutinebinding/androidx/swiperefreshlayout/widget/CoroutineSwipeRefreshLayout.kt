package com.github.satoshun.coroutinebinding.androidx.swiperefreshlayout.widget

import androidx.annotation.CheckResult
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

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
