package com.github.satoshun.coroutinebinding.support.v4.widget

import android.support.annotation.CheckResult
import android.support.v4.widget.NestedScrollView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import com.github.satoshun.coroutinebinding.view.ViewScrollChangeEvent
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of scroll-change events for view.
 */
@CheckResult
fun NestedScrollView.scrollChangeEvents(capacity: Int = 0): ReceiveChannel<ViewScrollChangeEvent> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
    safeOffer(ViewScrollChangeEvent(v, scrollX, scrollY, oldScrollX, oldScrollY))
  }
  onAfterClosed {
    val l: NestedScrollView.OnScrollChangeListener? = null
    setOnScrollChangeListener(l)
  }
  setOnScrollChangeListener(listener)
}
