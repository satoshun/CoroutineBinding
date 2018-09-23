package com.github.satoshun.coroutinebinding.androidx.recyclerview.widget

import android.view.View
import androidx.annotation.CheckResult
import androidx.recyclerview.widget.RecyclerView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

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
 * Suspend a of child attach state change event on RecyclerView.
 */
suspend fun RecyclerView.awaitChildAttachStateChangeEvent(): RecyclerViewChildAttachStateChangeEvent =
    suspendCancellableCoroutine { cont ->
      val listener = object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewDetachedFromWindow(view: View) {
          cont.resume(
              RecyclerViewChildDetachEvent(
                  this@awaitChildAttachStateChangeEvent,
                  view
              )
          )
          removeOnChildAttachStateChangeListener(this)
        }

        override fun onChildViewAttachedToWindow(view: View) {
          cont.resume(
              RecyclerViewChildAttachEvent(
                  this@awaitChildAttachStateChangeEvent,
                  view
              )
          )
          removeOnChildAttachStateChangeListener(this)
        }
      }
      cont.invokeOnCancellation {
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
 * Suspend a observable of scroll event on RecyclerView.
 */
suspend fun RecyclerView.awaitScrollEvent(): RecyclerViewScrollEvent =
    suspendCancellableCoroutine { cont ->
      val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          cont.resume(
              RecyclerViewScrollEvent(
                  recyclerView,
                  dx,
                  dy
              )
          )
          removeOnScrollListener(this)
        }
      }
      cont.invokeOnCancellation {
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

/**
 * Suspend a of scroll state changed on RecyclerView.
 */
suspend fun RecyclerView.awaitScrollStateChange(): Int =
    suspendCancellableCoroutine { cont ->
      val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
          cont.resume(newState)
          removeOnScrollListener(this)
        }
      }
      cont.invokeOnCancellation {
        removeOnScrollListener(listener)
      }
      addOnScrollListener(listener)
    }
