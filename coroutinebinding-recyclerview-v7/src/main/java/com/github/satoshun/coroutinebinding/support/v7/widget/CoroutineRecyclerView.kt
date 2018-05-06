@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.annotation.CheckResult
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of child attach state change events on [RecyclerView].
 */
@CheckResult
inline fun RecyclerView.childAttachStateChangeEvents(capacity: Int = 0): ReceiveChannel<RecyclerViewChildAttachStateChangeEvent> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = object : RecyclerView.OnChildAttachStateChangeListener {
    override fun onChildViewDetachedFromWindow(view: View) {
      safeOffer(RecyclerViewChildDetachEvent(this@childAttachStateChangeEvents, view))
    }

    override fun onChildViewAttachedToWindow(view: View) {
      safeOffer(RecyclerViewChildAttachEvent(this@childAttachStateChangeEvents, view))
    }
  }
  onAfterClosed {
    removeOnChildAttachStateChangeListener(listener)
  }
  addOnChildAttachStateChangeListener(listener)
}

sealed class RecyclerViewChildAttachStateChangeEvent(
    open val view: RecyclerView,
    open val child: View
)

data class RecyclerViewChildAttachEvent(
    override val view: RecyclerView,
    override val child: View
) : RecyclerViewChildAttachStateChangeEvent(view, child)

data class RecyclerViewChildDetachEvent(
    override val view: RecyclerView,
    override val child: View
) : RecyclerViewChildAttachStateChangeEvent(view, child)

/**
 * Create an observable of scroll events on [RecyclerView].
 */
@CheckResult
inline fun RecyclerView.scrollEvents(capacity: Int = 0): ReceiveChannel<RecyclerViewScrollEvent> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
      safeOffer(RecyclerViewScrollEvent(recyclerView, dx, dy))
    }
  }
  onAfterClosed {
    removeOnScrollListener(listener)
  }
  addOnScrollListener(listener)
}

data class RecyclerViewScrollEvent(
    val view: RecyclerView,
    val dx: Int,
    val dy: Int
)
