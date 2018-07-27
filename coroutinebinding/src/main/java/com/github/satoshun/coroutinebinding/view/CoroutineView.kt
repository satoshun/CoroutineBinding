@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.view

import android.support.annotation.CheckResult
import android.support.annotation.RequiresApi
import android.view.DragEvent
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import com.github.satoshun.coroutinebinding.cancelableChannel2
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

internal typealias Callable = () -> Boolean
internal typealias Predicate<T> = (T) -> Boolean

/**
 * Create an channel of to emit on view attach events.
 */
@CheckResult
fun View.attaches(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel2(capacity) {
  val listener = object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
      // do nothing
    }

    override fun onViewAttachedToWindow(v: View) {
      safeOffer(Unit)
    }
  }
  invokeOnClose {
    removeOnAttachStateChangeListener(listener)
  }
  addOnAttachStateChangeListener(listener)
}

/**
 * Create an channel of to emit on view detach events.
 */
@CheckResult
fun View.detaches(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel2(capacity) {
  val listener = object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
      safeOffer(Unit)
    }

    override fun onViewAttachedToWindow(v: View) {
      // do nothing
    }
  }
  invokeOnClose {
    removeOnAttachStateChangeListener(listener)
  }
  addOnAttachStateChangeListener(listener)
}

/**
 * Create an channel of to emit on view click events.
 */
@CheckResult
fun View.clicks(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel2(capacity) {
  val listener = View.OnClickListener {
    safeOffer(Unit)
  }
  invokeOnClose {
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
    cancelableChannel2(capacity) {
      val listener = View.OnDragListener { _, dragEvent ->
        if (handled(dragEvent)) {
          safeOffer(dragEvent)
        } else {
          false
        }
      }
      invokeOnClose {
        setOnDragListener(null)
      }
      setOnDragListener(listener)
    }

/**
 * Create an channel for draws on view
 */
@RequiresApi(16)
@CheckResult
fun View.draws(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel2(capacity) {
  val listener = ViewTreeObserver.OnDrawListener {
    safeOffer(Unit)
  }
  invokeOnClose {
    viewTreeObserver.removeOnDrawListener(listener)
  }
  viewTreeObserver.addOnDrawListener(listener)
}

/**
 * Create an channel of booleans representing the focus of view.
 */
@CheckResult
fun View.focusChanges(capacity: Int = 0): ReceiveChannel<Boolean> = cancelableChannel2(capacity) {
  val listener = View.OnFocusChangeListener { _, hasFocus ->
    safeOffer(hasFocus)
  }
  invokeOnClose {
    onFocusChangeListener = null
  }
  onFocusChangeListener = listener
}

/**
 * Create an channel of booleans representing the focus of view.
 */
@CheckResult
fun View.globalLayouts(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel2(capacity) {
  val listener = ViewTreeObserver.OnGlobalLayoutListener {
    safeOffer(Unit)
  }
  invokeOnClose {
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
    cancelableChannel2(capacity) {
      val listener = View.OnHoverListener { _, motionEvent ->
        if (handled(motionEvent)) {
          safeOffer(motionEvent)
        } else {
          false
        }
      }
      invokeOnClose {
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
): ReceiveChannel<T> = cancelableChannel2(capacity) {
  val listener = View.OnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
    safeOffer(creator(left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom))
  }
  invokeOnClose {
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
fun View.longClicks(capacity: Int = 0, handled: Callable): ReceiveChannel<Unit> = cancelableChannel2(capacity) {
  val listener = View.OnLongClickListener {
    if (handled()) {
      safeOffer(Unit)
    } else {
      false
    }
  }
  invokeOnClose {
    setOnLongClickListener(null)
  }
  setOnLongClickListener(listener)
}

/**
 * Create an channel for pre-draws on View.
 */
@CheckResult
fun View.preDraws(capacity: Int = 0, proceedDrawingPass: () -> Boolean): ReceiveChannel<Unit> =
    cancelableChannel2(capacity) {
      val listener = ViewTreeObserver.OnPreDrawListener {
        if (safeOffer(Unit)) {
          proceedDrawingPass()
        } else {
          true
        }
      }
      invokeOnClose {
        viewTreeObserver.removeOnPreDrawListener(listener)
      }
      viewTreeObserver.addOnPreDrawListener(listener)
    }

/**
 * Create an channel of scroll-change events for view.
 */
@RequiresApi(23)
@CheckResult
fun View.scrollChangeEvents(): ReceiveChannel<ViewScrollChangeEvent> = cancelableChannel2 {
  val listener = View.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
    safeOffer(ViewScrollChangeEvent(v, scrollX, scrollY, oldScrollX, oldScrollY))
  }
  invokeOnClose {
    setOnScrollChangeListener(null)
  }
  setOnScrollChangeListener(listener)
}

/**
 * Create an channel for systemUiVisibilityChanges on view.
 */
@CheckResult
fun View.systemUiVisibilityChanges(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel2(capacity) {
  val listener = View.OnSystemUiVisibilityChangeListener {
    safeOffer(it)
  }
  invokeOnClose {
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
    cancelableChannel2(capacity) {
      val listener = View.OnTouchListener { _, motionEvent ->
        if (handled(motionEvent)) {
          safeOffer(motionEvent)
        } else {
          false
        }
      }
      invokeOnClose {
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
    cancelableChannel2(capacity) {
      val listener = View.OnKeyListener { _, _, event ->
        if (handled(event)) {
          safeOffer(event)
        } else {
          false
        }
      }
      invokeOnClose {
        setOnKeyListener(null)
      }
      setOnKeyListener(listener)
    }
