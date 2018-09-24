package com.github.satoshun.coroutinebinding.androidx.core.widget

import androidx.annotation.CheckResult
import androidx.core.widget.NestedScrollView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import com.github.satoshun.coroutinebinding.view.ViewScrollChangeEvent
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel of scroll-change events for view.
 */
@CheckResult
fun NestedScrollView.scrollChangeEvents(capacity: Int = 0): ReceiveChannel<ViewScrollChangeEvent> =
    cancelableChannel(capacity) {
      val listener = NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
        safeOffer(ViewScrollChangeEvent(v, scrollX, scrollY, oldScrollX, oldScrollY))
      }
      invokeOnCloseOnMain {
        val l: NestedScrollView.OnScrollChangeListener? = null
        setOnScrollChangeListener(l)
      }
      setOnScrollChangeListener(listener)
    }

/**
 * Suspend a of scroll-change event for view.
 */
suspend fun NestedScrollView.awaitScrollChangeEvent(): ViewScrollChangeEvent =
    suspendCancellableCoroutine { cont ->
      val listener = NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
        cont.resume(ViewScrollChangeEvent(v, scrollX, scrollY, oldScrollX, oldScrollY))
        val l: NestedScrollView.OnScrollChangeListener? = null
        setOnScrollChangeListener(l)
      }
      cont.invokeOnCancellation {
        val l: NestedScrollView.OnScrollChangeListener? = null
        setOnScrollChangeListener(l)
      }
      setOnScrollChangeListener(listener)
    }
