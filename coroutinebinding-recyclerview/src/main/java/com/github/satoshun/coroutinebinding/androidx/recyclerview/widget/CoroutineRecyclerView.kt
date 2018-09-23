package com.github.satoshun.coroutinebinding.androidx.recyclerview.widget

import android.view.View
import androidx.annotation.CheckResult
import androidx.recyclerview.widget.RecyclerView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of child attach state change events on RecyclerView.
 */
@CheckResult
fun RecyclerView.childAttachStateChangeEvents(capacity: Int = 0): ReceiveChannel<RecyclerViewChildAttachStateChangeEvent> =
    cancelableChannel(capacity) {
      val listener = object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewDetachedFromWindow(view: View) {
          safeOffer(
              RecyclerViewChildDetachEvent(
                  this@childAttachStateChangeEvents,
                  view
              )
          )
        }

        override fun onChildViewAttachedToWindow(view: View) {
          safeOffer(
              RecyclerViewChildAttachEvent(
                  this@childAttachStateChangeEvents,
                  view
              )
          )
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
    cancelableChannel(capacity) {
      val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          safeOffer(
              RecyclerViewScrollEvent(
                  recyclerView,
                  dx,
                  dy
              )
          )
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
    cancelableChannel(capacity) {
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