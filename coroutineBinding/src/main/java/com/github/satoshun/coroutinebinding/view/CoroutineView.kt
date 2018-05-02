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
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

internal typealias Callable = () -> Boolean
internal typealias Predicate<T> = (T) -> Boolean

/**
 * Create an channel of to emit on [View] attach events.
 */
@CheckResult
inline fun View.attaches(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
      // do nothing
    }

    override fun onViewAttachedToWindow(v: View) {
      safeOffer(Unit)
    }
  }
  it {
    removeOnAttachStateChangeListener(listener)
  }
  addOnAttachStateChangeListener(listener)
}

/**
 * Create an channel of to emit on [View] detach events.
 */
@CheckResult
inline fun View.detaches(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
      safeOffer(Unit)
    }

    override fun onViewAttachedToWindow(v: View) {
      // do nothing
    }
  }
  it {
    removeOnAttachStateChangeListener(listener)
  }
  addOnAttachStateChangeListener(listener)
}

/**
 * Create an channel of to emit on [View] click events.
 */
@CheckResult
inline fun View.clicks(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = View.OnClickListener {
    safeOffer(Unit)
  }
  it {
    setOnClickListener(null)
  }
  setOnClickListener(listener)
}

/**
 * Create an channel of [DragEvent] for drags on [View]
 */
@CheckResult
inline fun View.drags(capacity: Int = 0): ReceiveChannel<DragEvent> = drags(capacity) { true }

/**
 * Create an channel of [DragEvent] for drags on [View]
 */
@CheckResult
inline fun View.drags(
    capacity: Int = 0,
    crossinline handled: Predicate<in DragEvent>
): ReceiveChannel<DragEvent> = cancelableChannel(capacity) {
  val listener = View.OnDragListener { _, dragEvent ->
    if (handled(dragEvent)) {
      safeOffer(dragEvent)
    } else {
      false
    }
  }
  it {
    setOnDragListener(null)
  }
  setOnDragListener(listener)
}

/**
 * Create an channel for draws on [View]
 */
@RequiresApi(16)
@CheckResult
inline fun View.draws(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = ViewTreeObserver.OnDrawListener {
    safeOffer(Unit)
  }
  it {
    viewTreeObserver.removeOnDrawListener(listener)
  }
  viewTreeObserver.addOnDrawListener(listener)
}

/**
 * Create an channel of booleans representing the focus of [View].
 */
@CheckResult
inline fun View.focusChanges(capacity: Int = 0): ReceiveChannel<Boolean> = cancelableChannel(capacity) {
  val listener = View.OnFocusChangeListener { _, hasFocus ->
    safeOffer(hasFocus)
  }
  it {
    onFocusChangeListener = null
  }
  onFocusChangeListener = listener
}

/**
 * Create an channel of booleans representing the focus of [View].
 */
@CheckResult
inline fun View.globalLayouts(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = ViewTreeObserver.OnGlobalLayoutListener {
    safeOffer(Unit)
  }
  it {
    viewTreeObserver.addOnGlobalLayoutListener(listener)
  }
  viewTreeObserver.addOnGlobalLayoutListener(listener)
}

/**
 * Create an channel of hover events for [View].
 */
@CheckResult
inline fun View.hovers(capacity: Int = 0): ReceiveChannel<MotionEvent> = hovers(capacity) { true }

/**
 * Create an channel of hover events for [View].
 */
@CheckResult
inline fun View.hovers(
    capacity: Int = 0,
    crossinline handled: Predicate<in MotionEvent>
): ReceiveChannel<MotionEvent> = cancelableChannel(capacity) {
  val listener = View.OnHoverListener { _, motionEvent ->
    if (handled(motionEvent)) {
      safeOffer(motionEvent)
    } else {
      false
    }
  }
  it {
    setOnHoverListener(null)
  }
  setOnHoverListener(listener)
}

/**
 * Create an channel of layoutChange events for [View].
 */
@CheckResult
inline fun View.layoutChanges(capacity: Int = 0): ReceiveChannel<Unit> = layoutChangeEvents(capacity) { _, _, _, _, _, _, _, _ -> Unit }

/**
 * Create an channel of layoutChange events for [View].
 */
@CheckResult
inline fun View.layoutChangeEvents(capacity: Int = 0): ReceiveChannel<ViewLayoutChangeEvent> = layoutChangeEvents(capacity) { left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
  ViewLayoutChangeEvent(left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom)
}

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
 * Create an channel of layoutChange events for [View].
 */
@CheckResult
inline fun <T> View.layoutChangeEvents(
    capacity: Int = 0,
    crossinline creator: (Int, Int, Int, Int, Int, Int, Int, Int) -> T
): ReceiveChannel<T> = cancelableChannel(capacity) {
  val listener = View.OnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
    safeOffer(creator(left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom))
  }
  it {
    removeOnLayoutChangeListener(listener)
  }
  addOnLayoutChangeListener(listener)
}

/**
 * Create an channel of longClick events for [View].
 */
@CheckResult
inline fun View.longClicks(capacity: Int = 0): ReceiveChannel<Unit> = longClicks(capacity) { true }

/**
 * Create an channel of longClick events for [View].
 */
@CheckResult
inline fun View.longClicks(
    capacity: Int = 0,
    crossinline handled: Callable
): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = View.OnLongClickListener {
    if (handled()) {
      safeOffer(Unit)
    } else {
      false
    }
  }
  it {
    setOnLongClickListener(null)
  }
  setOnLongClickListener(listener)
}

/**
 * Create an channel for pre-draws on [View].
 */
@CheckResult
inline fun View.preDraws(
    capacity: Int = 0,
    crossinline proceedDrawingPass: () -> Boolean
): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = ViewTreeObserver.OnPreDrawListener {
    if (safeOffer(Unit)) {
      proceedDrawingPass()
    } else {
      true
    }
  }
  it {
    viewTreeObserver.removeOnPreDrawListener(listener)
  }
  viewTreeObserver.addOnPreDrawListener(listener)
}

/**
 * todo
 */
@RequiresApi(23)
@CheckResult
inline fun View.scrollChangeEvents(): ReceiveChannel<ViewScrollChangeEvent> = cancelableChannel {
  val listener = View.OnScrollChangeListener { _, scrollX, scrollY, oldScrollX, oldScrollY ->
    safeOffer(ViewScrollChangeEvent(scrollX, scrollY, oldScrollX, oldScrollY))
  }
  it {
    setOnScrollChangeListener(null)
  }
  setOnScrollChangeListener(listener)
}

data class ViewScrollChangeEvent(
    val scrollX: Int,
    val scrollY: Int,
    val oldScrollX: Int,
    val oldScrollY: Int
)

/**
 * Create an channel for systemUiVisibilityChanges on [View].
 */
@CheckResult
inline fun View.systemUiVisibilityChanges(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel(capacity) {
  val listener = View.OnSystemUiVisibilityChangeListener {
    safeOffer(it)
  }
  it {
    setOnSystemUiVisibilityChangeListener(null)
  }
  setOnSystemUiVisibilityChangeListener(listener)
}

/**
 * Create an channel of touch events for [View].
 */
@CheckResult
inline fun View.touches(capacity: Int = 0): ReceiveChannel<MotionEvent> = touches(capacity) { true }

/**
 * Create an channel of touch events for [View].
 */
@CheckResult
inline fun View.touches(
    capacity: Int = 0,
    crossinline handled: Predicate<in MotionEvent>
): ReceiveChannel<MotionEvent> = cancelableChannel(capacity) {
  val listener = View.OnTouchListener { _, motionEvent ->
    if (handled(motionEvent)) {
      safeOffer(motionEvent)
    } else {
      false
    }
  }
  it {
    setOnTouchListener(null)
  }
  setOnTouchListener(listener)
}

/**
 * Create an channel of key events for [View].
 */
@CheckResult
inline fun View.keys(capacity: Int = 0): ReceiveChannel<KeyEvent> = keys(capacity) { true }

/**
 * Create an channel of key events for [View].
 */
@CheckResult
inline fun View.keys(
    capacity: Int = 0,
    crossinline handled: Predicate<in KeyEvent>
): ReceiveChannel<KeyEvent> = cancelableChannel(capacity) {
  val listener = View.OnKeyListener { _, _, event ->
    if (handled(event)) {
      safeOffer(event)
    } else {
      false
    }
  }
  it {
    setOnKeyListener(null)
  }
  setOnKeyListener(listener)
}
