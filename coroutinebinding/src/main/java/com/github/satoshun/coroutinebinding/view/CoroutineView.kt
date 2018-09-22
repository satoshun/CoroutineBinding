@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.view

import android.support.annotation.CheckResult
import android.support.annotation.RequiresApi
import android.view.DragEvent
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

internal typealias Callable = () -> Boolean
internal typealias Predicate<T> = (T) -> Boolean

/**
 * Create an channel of to emit on view attach events.
 */
@CheckResult
fun View.attaches(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
      // do nothing
    }

    override fun onViewAttachedToWindow(v: View) {
      safeOffer(Unit)
    }
  }
  invokeOnCloseOnMain {
    removeOnAttachStateChangeListener(listener)
  }
  addOnAttachStateChangeListener(listener)
}

/**
 * Suspend for view attach event.
 */
suspend fun View.awaitAttach(): Unit = suspendCancellableCoroutine { cont ->
  val listener = object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
      // do nothing
    }

    override fun onViewAttachedToWindow(v: View) {
      cont.resume(Unit)
      removeOnAttachStateChangeListener(this)
    }
  }

  cont.invokeOnCancellation {
    // todo check mainthread?
    removeOnAttachStateChangeListener(listener)
  }
  addOnAttachStateChangeListener(listener)
}

/**
 * Create an channel of to emit on view detach events.
 */
@CheckResult
fun View.detaches(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
      safeOffer(Unit)
    }

    override fun onViewAttachedToWindow(v: View) {
      // do nothing
    }
  }
  invokeOnCloseOnMain {
    removeOnAttachStateChangeListener(listener)
  }
  addOnAttachStateChangeListener(listener)
}

/**
 * Suspend for view detach event.
 */
suspend fun View.awaitDetach(): Unit = suspendCancellableCoroutine { cont ->
  val listener = object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
      cont.resume(Unit)
      removeOnAttachStateChangeListener(this)
    }

    override fun onViewAttachedToWindow(v: View) {
      // do nothing
    }
  }
  cont.invokeOnCancellation {
    // todo check mainthread?
    removeOnAttachStateChangeListener(listener)
  }
  addOnAttachStateChangeListener(listener)
}

/**
 * Create an channel of to emit on view click events.
 */
@CheckResult
fun View.clicks(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = View.OnClickListener {
    safeOffer(Unit)
  }
  invokeOnCloseOnMain {
    setOnClickListener(null)
  }
  setOnClickListener(listener)
}

/**
 * Suspend for view click event.
 */
suspend fun View.click(): Unit = suspendCancellableCoroutine { cont ->
  val listener = View.OnClickListener {
    cont.resume(Unit)
    setOnClickListener(null)
  }
  cont.invokeOnCancellation {
    // todo check mainthread?
    setOnClickListener(null)
  }
  setOnClickListener(listener)
}

/**
 * Create an channel of DragEvent for drags on View
 */
@CheckResult
inline fun View.drags(capacity: Int = 0): ReceiveChannel<DragEvent> = drags(capacity) { true }

/**
 * Create an channel of DragEvent for drags on view
 */
@CheckResult
fun View.drags(capacity: Int = 0, handled: Predicate<in DragEvent>): ReceiveChannel<DragEvent> =
    cancelableChannel(capacity) {
      val listener = View.OnDragListener { _, dragEvent ->
        if (handled(dragEvent)) {
          safeOffer(dragEvent)
        } else {
          false
        }
      }
      invokeOnCloseOnMain {
        setOnDragListener(null)
      }
      setOnDragListener(listener)
    }

/**
 * Suspend for drags on View
 */
suspend fun View.drag(): DragEvent = drag { true }

/**
 * Suspend for drags on view
 */
suspend fun View.drag(handled: Predicate<in DragEvent>): DragEvent = suspendCancellableCoroutine { cont ->
  val listener = View.OnDragListener { _, dragEvent ->
    if (handled(dragEvent)) {
      cont.resume(dragEvent)
      setOnDragListener(null)
      true
    } else {
      false
    }
  }
  cont.invokeOnCancellation {
    setOnDragListener(null)
  }
  setOnDragListener(listener)
}

/**
 * Create an channel for draws on view
 */
@RequiresApi(16)
@CheckResult
fun View.draws(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = ViewTreeObserver.OnDrawListener {
    safeOffer(Unit)
  }
  invokeOnCloseOnMain {
    viewTreeObserver.removeOnDrawListener(listener)
  }
  viewTreeObserver.addOnDrawListener(listener)
}

/**
 * Suspend for draws on view
 */
@RequiresApi(16)
suspend fun View.draw(): Unit = suspendCancellableCoroutine { cont ->
  val listener = object : ViewTreeObserver.OnDrawListener {
    override fun onDraw() {
      cont.resume(Unit)
      viewTreeObserver.removeOnDrawListener(this)
    }
  }
  cont.invokeOnCancellation {
    viewTreeObserver.removeOnDrawListener(listener)
  }
  viewTreeObserver.addOnDrawListener(listener)
}

/**
 * Create an channel of booleans representing the focus of view.
 */
@CheckResult
fun View.focusChanges(capacity: Int = 0): ReceiveChannel<Boolean> = cancelableChannel(capacity) {
  val listener = View.OnFocusChangeListener { _, hasFocus ->
    safeOffer(hasFocus)
  }
  invokeOnCloseOnMain {
    onFocusChangeListener = null
  }
  onFocusChangeListener = listener
}

/**
 * Suspend the focus of view.
 */
suspend fun View.focusChange(): Boolean = suspendCancellableCoroutine { cont ->
  val listener = View.OnFocusChangeListener { _, hasFocus ->
    cont.resume(hasFocus)
    onFocusChangeListener = null
  }
  cont.invokeOnCancellation {
    onFocusChangeListener = null
  }
  onFocusChangeListener = listener
}

/**
 * Create an channel of booleans representing the focus of view.
 */
@CheckResult
fun View.globalLayouts(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = ViewTreeObserver.OnGlobalLayoutListener {
    safeOffer(Unit)
  }
  invokeOnCloseOnMain {
    viewTreeObserver.addOnGlobalLayoutListener(listener)
  }
  viewTreeObserver.addOnGlobalLayoutListener(listener)
}

/**
 * Create an channel of hover events for view.
 */
@CheckResult
inline fun View.hovers(capacity: Int = 0): ReceiveChannel<MotionEvent> = hovers(capacity) { true }

/**
 * Create an channel of hover events for view.
 */
@CheckResult
fun View.hovers(capacity: Int = 0, handled: Predicate<in MotionEvent>): ReceiveChannel<MotionEvent> =
    cancelableChannel(capacity) {
      val listener = View.OnHoverListener { _, motionEvent ->
        if (handled(motionEvent)) {
          safeOffer(motionEvent)
        } else {
          false
        }
      }
      invokeOnCloseOnMain {
        setOnHoverListener(null)
      }
      setOnHoverListener(listener)
    }

/**
 * Create an channel of layoutChange events for View.
 */
@CheckResult
inline fun View.layoutChanges(capacity: Int = 0): ReceiveChannel<Unit> =
    layoutChangeEvents(capacity) { _, _, _, _, _, _, _, _ -> Unit }

/**
 * Create an channel of layoutChange events for View.
 */
@CheckResult
inline fun View.layoutChangeEvents(capacity: Int = 0): ReceiveChannel<ViewLayoutChangeEvent> =
    layoutChangeEvents(capacity) { left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
      ViewLayoutChangeEvent(left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom)
    }

/**
 * A layout-change event on a view.
 */
data class ViewLayoutChangeEvent(
  val left: Int,
  val top: Int,
  val right: Int,
  val bottom: Int,
  val oldLeft: Int,
  val oldTop: Int,
  val oldRight: Int,
  val oldBottom: Int
)

/**
 * Create an channel of layoutChange events for View.
 */
@CheckResult
fun <T> View.layoutChangeEvents(
  capacity: Int = 0,
  creator: (Int, Int, Int, Int, Int, Int, Int, Int) -> T
): ReceiveChannel<T> = cancelableChannel(capacity) {
  val listener = View.OnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
    safeOffer(creator(left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom))
  }
  invokeOnCloseOnMain {
    removeOnLayoutChangeListener(listener)
  }
  addOnLayoutChangeListener(listener)
}

/**
 * Create an channel of longClick events for View.
 */
@CheckResult
inline fun View.longClicks(capacity: Int = 0): ReceiveChannel<Unit> = longClicks(capacity) { true }

/**
 * Create an channel of longClick events for View.
 */
@CheckResult
fun View.longClicks(capacity: Int = 0, handled: Callable): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = View.OnLongClickListener {
    if (handled()) {
      safeOffer(Unit)
    } else {
      false
    }
  }
  invokeOnCloseOnMain {
    setOnLongClickListener(null)
  }
  setOnLongClickListener(listener)
}

/**
 * Create an channel for pre-draws on View.
 */
@CheckResult
fun View.preDraws(capacity: Int = 0, proceedDrawingPass: () -> Boolean): ReceiveChannel<Unit> =
    cancelableChannel(capacity) {
      val listener = ViewTreeObserver.OnPreDrawListener {
        if (safeOffer(Unit)) {
          proceedDrawingPass()
        } else {
          true
        }
      }
      invokeOnCloseOnMain {
        viewTreeObserver.removeOnPreDrawListener(listener)
      }
      viewTreeObserver.addOnPreDrawListener(listener)
    }

/**
 * Create an channel of scroll-change events for view.
 */
@RequiresApi(23)
@CheckResult
fun View.scrollChangeEvents(capacity: Int = 0): ReceiveChannel<ViewScrollChangeEvent> = cancelableChannel(capacity) {
  val listener = View.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
    safeOffer(ViewScrollChangeEvent(v, scrollX, scrollY, oldScrollX, oldScrollY))
  }
  invokeOnCloseOnMain {
    setOnScrollChangeListener(null)
  }
  setOnScrollChangeListener(listener)
}

/**
 * Create an channel for systemUiVisibilityChanges on view.
 */
@CheckResult
fun View.systemUiVisibilityChanges(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel(capacity) {
  val listener = View.OnSystemUiVisibilityChangeListener {
    safeOffer(it)
  }
  invokeOnCloseOnMain {
    setOnSystemUiVisibilityChangeListener(null)
  }
  setOnSystemUiVisibilityChangeListener(listener)
}

/**
 * Create an channel of touch events for view.
 */
@CheckResult
inline fun View.touches(capacity: Int = 0): ReceiveChannel<MotionEvent> = touches(capacity) { true }

/**
 * Create an channel of touch events for view.
 */
@CheckResult
fun View.touches(capacity: Int = 0, handled: Predicate<in MotionEvent>): ReceiveChannel<MotionEvent> =
    cancelableChannel(capacity) {
      val listener = View.OnTouchListener { _, motionEvent ->
        if (handled(motionEvent)) {
          safeOffer(motionEvent)
        } else {
          false
        }
      }
      invokeOnCloseOnMain {
        setOnTouchListener(null)
      }
      setOnTouchListener(listener)
    }

/**
 * Create an channel of key events for view.
 */
@CheckResult
inline fun View.keys(capacity: Int = 0): ReceiveChannel<KeyEvent> = keys(capacity) { true }

/**
 * Create an channel of key events for view.
 */
@CheckResult
fun View.keys(capacity: Int = 0, handled: Predicate<in KeyEvent>): ReceiveChannel<KeyEvent> =
    cancelableChannel(capacity) {
      val listener = View.OnKeyListener { _, _, event ->
        if (handled(event)) {
          safeOffer(event)
        } else {
          false
        }
      }
      invokeOnCloseOnMain {
        setOnKeyListener(null)
      }
      setOnKeyListener(listener)
    }
