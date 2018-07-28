package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.annotation.CheckResult
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.satoshun.coroutinebinding.cancelableChannel2
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of child attach state change events on RecyclerView.
 */
@CheckResult
fun RecyclerView.childAttachStateChangeEvents(capacity: Int = 0): ReceiveChannel<RecyclerViewChildAttachStateChangeEvent> =
    cancelableChannel2(capacity) {
      val listener = object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewDetachedFromWindow(view: View) {
          safeOffer(RecyclerViewChildDetachEvent(this@childAttachStateChangeEvents, view))
        }

        override fun onChildViewAttachedToWindow(view: View) {
          safeOffer(RecyclerViewChildAttachEvent(this@childAttachStateChangeEvents, view))
        }
      }
      invokeOnCloseOnMain {
        removeOnChildAttachStateChangeListener(listener)
      }
      addOnChildAttachStateChangeListener(listener)
    }

/**
 * A child attach state event on RecyclerView
 */
sealed class RecyclerViewChildAttachStateChangeEvent {
  abstract val view: RecyclerView
  abstract val child: View
}

/**
 * A child attach event on RecyclerView
 */
data class RecyclerViewChildAttachEvent(
  override val view: RecyclerView,
  override val child: View
) : RecyclerViewChildAttachStateChangeEvent()

/**
 * A child detach event on RecyclerView
 */
data class RecyclerViewChildDetachEvent(
  override val view: RecyclerView,
  override val child: View
) : RecyclerViewChildAttachStateChangeEvent()

/**
 * Create an observable of scroll events on RecyclerView.
 */
@CheckResult
fun RecyclerView.scrollEvents(capacity: Int = 0): ReceiveChannel<RecyclerViewScrollEvent> =
    cancelableChannel2(capacity) {
      val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          safeOffer(RecyclerViewScrollEvent(recyclerView, dx, dy))
        }
      }
      invokeOnCloseOnMain {
        removeOnScrollListener(listener)
      }
      addOnScrollListener(listener)
    }

/**
 * A scroll event on RecyclerView.
 */
data class RecyclerViewScrollEvent(
  val view: RecyclerView,
  val dx: Int,
  val dy: Int
)

/**
 * Create an observable of scroll state changed on RecyclerView.
 */
@CheckResult
fun RecyclerView.scrollStateChanges(capacity: Int = 0): ReceiveChannel<Int> =
    cancelableChannel2(capacity) {
      val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
          safeOffer(newState)
        }
      }
      invokeOnCloseOnMain {
        removeOnScrollListener(listener)
      }
      addOnScrollListener(listener)
    }
