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

private typealias Callable = () -> Boolean
private typealias Predicate<T> = (T) -> Boolean

/**
 * todo
 */
@CheckResult
inline fun View.attaches(): ReceiveChannel<Unit> = cancelableChannel {
  val listener = object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
      // do nothing
    }

    override fun onViewAttachedToWindow(v: View) {
      safeOffer(Unit)
    }
  }
  onAfterClosed = {
    removeOnAttachStateChangeListener(listener)
  }
  addOnAttachStateChangeListener(listener)
}

/**
 * todo
 */
@CheckResult
inline fun View.detaches(): ReceiveChannel<Unit> = cancelableChannel {
  val listener = object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
      safeOffer(Unit)
    }

    override fun onViewAttachedToWindow(v: View) {
      // do nothing
    }
  }
  onAfterClosed = {
    removeOnAttachStateChangeListener(listener)
  }
  addOnAttachStateChangeListener(listener)
}

/**
 * todo
 */
@CheckResult
inline fun View.clicks(): ReceiveChannel<Unit> = cancelableChannel {
  val listener = View.OnClickListener {
    safeOffer(Unit)
  }
  onAfterClosed = {
    setOnClickListener(null)
  }
  setOnClickListener(listener)
}

@CheckResult
inline fun View.drags(): ReceiveChannel<DragEvent> = drags { true }

@CheckResult
inline fun View.drags(crossinline handled: Predicate<in DragEvent>): ReceiveChannel<DragEvent> = cancelableChannel {
  val listener = View.OnDragListener { _, dragEvent ->
    if (handled(dragEvent)) {
      safeOffer(dragEvent)
    } else {
      false
    }
  }
  onAfterClosed = {
    setOnDragListener(null)
  }
  setOnDragListener(listener)
}

/**
 * todo
 */
@RequiresApi(16)
@CheckResult
inline fun View.draws(): ReceiveChannel<Unit> = cancelableChannel {
  val listener = ViewTreeObserver.OnDrawListener {
    safeOffer(Unit)
  }
  onAfterClosed = {
    viewTreeObserver.removeOnDrawListener(listener)
  }
  viewTreeObserver.addOnDrawListener(listener)
}

/**
 * todo
 */
@CheckResult
inline fun View.focusChanges(): ReceiveChannel<Boolean> = cancelableChannel {
  val listener = View.OnFocusChangeListener { _, hasFocus ->
    safeOffer(hasFocus)
  }
  onAfterClosed = {
    onFocusChangeListener = null
  }
  onFocusChangeListener = listener
}

/**
 * todo
 */
@CheckResult
inline fun View.globalLayouts(): ReceiveChannel<Unit> = cancelableChannel {
  val listener = ViewTreeObserver.OnGlobalLayoutListener {
    safeOffer(Unit)
  }
  onAfterClosed = {
    viewTreeObserver.addOnGlobalLayoutListener(listener)
  }
  viewTreeObserver.addOnGlobalLayoutListener(listener)
}

/**
 * todo
 */
@CheckResult
inline fun View.hovers(): ReceiveChannel<MotionEvent> = hovers { true }

@CheckResult
inline fun View.hovers(crossinline handled: Predicate<in MotionEvent>): ReceiveChannel<MotionEvent> = cancelableChannel {
  val listener = View.OnHoverListener { _, motionEvent ->
    if (handled(motionEvent)) {
      safeOffer(motionEvent)
    } else {
      false
    }
  }
  onAfterClosed = {
    setOnHoverListener(null)
  }
  setOnHoverListener(listener)
}

/**
 * todo
 */
@CheckResult
inline fun View.layoutChanges(): ReceiveChannel<Unit> = layoutChangeEvents { _, _, _, _, _, _, _, _ -> Unit }

@CheckResult
inline fun View.layoutChangeEvents(): ReceiveChannel<ViewLayoutChangeEvent> = layoutChangeEvents { left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
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

@CheckResult
inline fun <T> View.layoutChangeEvents(
    crossinline creator: (Int, Int, Int, Int, Int, Int, Int, Int) -> T
): ReceiveChannel<T> = cancelableChannel {
  val listener = View.OnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
    safeOffer(creator(left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom))
  }
  onAfterClosed = {
    removeOnLayoutChangeListener(listener)
  }
  addOnLayoutChangeListener(listener)
}

/**
 * todo
 */
@CheckResult
inline fun View.longClicks(): ReceiveChannel<Unit> = longClicks { true }

@CheckResult
inline fun View.longClicks(crossinline handled: Callable): ReceiveChannel<Unit> = cancelableChannel {
  val listener = View.OnLongClickListener {
    if (handled()) {
      safeOffer(Unit)
    } else {
      false
    }
  }
  onAfterClosed = {
    setOnLongClickListener(null)
  }
  setOnLongClickListener(listener)
}

/**
 * todo
 */
@CheckResult
inline fun View.preDraws(crossinline proceedDrawingPass: () -> Boolean): ReceiveChannel<Unit> = cancelableChannel {
  val listener = ViewTreeObserver.OnPreDrawListener {
    if (safeOffer(Unit)) {
      proceedDrawingPass()
    } else {
      true
    }
  }
  onAfterClosed = {
    viewTreeObserver.removeOnPreDrawListener(listener)
  }
  viewTreeObserver.addOnPreDrawListener(listener)
}

@RequiresApi(23)
@CheckResult
inline fun View.scrollChangeEvents(): ReceiveChannel<ViewScrollChangeEvent> = cancelableChannel {
  val listener = View.OnScrollChangeListener { _, scrollX, scrollY, oldScrollX, oldScrollY ->
    safeOffer(ViewScrollChangeEvent(scrollX, scrollY, oldScrollX, oldScrollY))
  }
  onAfterClosed = {
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
 * todo
 */
@CheckResult
inline fun View.systemUiVisibilityChanges(): ReceiveChannel<Int> = cancelableChannel {
  val listener = View.OnSystemUiVisibilityChangeListener {
    safeOffer(it)
  }
  onAfterClosed = {
    setOnSystemUiVisibilityChangeListener(listener)
  }
  setOnSystemUiVisibilityChangeListener(listener)
}

/**
 * todo
 */
@CheckResult
inline fun View.touches(): ReceiveChannel<MotionEvent> = touches { true }

@CheckResult
inline fun View.touches(crossinline handled: Predicate<in MotionEvent>): ReceiveChannel<MotionEvent> = cancelableChannel {
  val listener = View.OnTouchListener { _, motionEvent ->
    if (handled(motionEvent)) {
      safeOffer(motionEvent)
    } else {
      false
    }
  }
  onAfterClosed = {
    setOnTouchListener(null)
  }
  setOnTouchListener(listener)
}

/**
 * todo
 */
@CheckResult
inline fun View.keys(): ReceiveChannel<KeyEvent> = keys { true }

@CheckResult
inline fun View.keys(crossinline handled: Predicate<in KeyEvent>): ReceiveChannel<KeyEvent> = cancelableChannel {
  val listener = View.OnKeyListener { _, _, event ->
    if (handled(event)) {
      safeOffer(event)
    } else {
      false
    }
  }
  onAfterClosed = {
    setOnKeyListener(null)
  }
  setOnKeyListener(listener)
}
