package com.github.satoshun.coroutinebinding.view

import android.view.DragEvent
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.CheckResult
import androidx.annotation.RequiresApi
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
suspend fun View.awaitClick(): Unit = suspendCancellableCoroutine { cont ->
  val listener = View.OnClickListener {
    cont.resume(Unit)
    setOnClickListener(null)
  }
  cont.invokeOnCancellation {
    setOnClickListener(null)
  }
  setOnClickListener(listener)
}

/**
 * Create an channel of DragEvent for drags on View
 */
@CheckResult
fun View.drags(capacity: Int = 0): ReceiveChannel<DragEvent> = drags(capacity) { true }

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
suspend fun View.awaitDrag(): DragEvent = awaitDrag { true }

/**
 * Suspend for drags on view
 */
suspend fun View.awaitDrag(handled: Predicate<in DragEvent>): DragEvent = suspendCancellableCoroutine { cont ->
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
suspend fun View.awaitDraw(): Unit = suspendCancellableCoroutine { cont ->
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
suspend fun View.awaitFocusChange(): Boolean = suspendCancellableCoroutine { cont ->
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
    viewTreeObserver.removeGlobalOnLayoutListener(listener)
  }
  viewTreeObserver.addOnGlobalLayoutListener(listener)
}

/**
 * Suspend the focus of view.
 */
suspend fun View.awaitGlobalLayout(): Unit = suspendCancellableCoroutine { cont ->
  val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
    override fun onGlobalLayout() {
      cont.resume(Unit)
      viewTreeObserver.removeGlobalOnLayoutListener(this)
    }
  }
  cont.invokeOnCancellation {
    viewTreeObserver.removeGlobalOnLayoutListener(listener)
  }
  viewTreeObserver.addOnGlobalLayoutListener(listener)
}

/**
 * Create an channel of hover events for view.
 */
@CheckResult
fun View.hovers(capacity: Int = 0): ReceiveChannel<MotionEvent> = hovers(capacity) { true }

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
 * Suspend hover event for view.
 */
suspend fun View.awaitHover(): MotionEvent = awaitHover { true }

/**
 * Suspend hover event for view.
 */
suspend fun View.awaitHover(handled: Predicate<in MotionEvent>): MotionEvent = suspendCancellableCoroutine { cont ->
  val listener = View.OnHoverListener { _, motionEvent ->
    if (handled(motionEvent)) {
      cont.resume(motionEvent)
      setOnHoverListener(null)
      true
    } else {
      false
    }
  }
  cont.invokeOnCancellation {
    setOnHoverListener(null)
  }
  setOnHoverListener(listener)
}

/**
 * Create an channel of layoutChange events for View.
 */
@CheckResult
fun View.layoutChanges(capacity: Int = 0): ReceiveChannel<Unit> =
    layoutChangeEvents(capacity) { _, _, _, _, _, _, _, _ -> Unit }

/**
 * Create an channel of layoutChange events for View.
 */
@CheckResult
fun View.layoutChangeEvents(capacity: Int = 0): ReceiveChannel<ViewLayoutChangeEvent> =
    layoutChangeEvents(capacity) { left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
      ViewLayoutChangeEvent(left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom)
    }

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
 * Suspend layoutChange event for View.
 */
suspend fun View.awaitLayoutChange(): Unit =
    awaitLayoutChangeEvent { _, _, _, _, _, _, _, _ -> Unit }

/**
 * Suspend layoutChange event for View.
 */
suspend fun View.awaitLayoutChangeEvent(): ViewLayoutChangeEvent =
    awaitLayoutChangeEvent { left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
      ViewLayoutChangeEvent(left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom)
    }

/**
 * Suspend an layoutChange event for View.
 */
suspend fun <T> View.awaitLayoutChangeEvent(
  creator: (Int, Int, Int, Int, Int, Int, Int, Int) -> T
): T = suspendCancellableCoroutine { cont ->
  val listener = object : View.OnLayoutChangeListener {
    override fun onLayoutChange(
      v: View?, left: Int, top: Int, right: Int, bottom: Int,
      oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
    ) {
      cont.resume(creator(left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom))
      removeOnLayoutChangeListener(this)
    }
  }
  cont.invokeOnCancellation {
    removeOnLayoutChangeListener(listener)
  }
  addOnLayoutChangeListener(listener)
}

/**
 * Create an channel of longClick events for View.
 */
@CheckResult
fun View.longClicks(capacity: Int = 0): ReceiveChannel<Unit> = longClicks(capacity) { true }

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
 * Suspend a longClick event for View.
 */
suspend fun View.awaitLongClick(): Unit = awaitLongClick { true }

/**
 * Suspend a longClick event for View.
 */
suspend fun View.awaitLongClick(handled: Callable): Unit = suspendCancellableCoroutine { cont ->
  val listener = View.OnLongClickListener {
    if (handled()) {
      cont.resume(Unit)
      setOnLongClickListener(null)
      true
    } else {
      false
    }
  }
  cont.invokeOnCancellation {
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
 * Suspend a pre-draws event on View.
 */
suspend fun View.awaitPreDraw(proceedDrawingPass: () -> Boolean): Unit = suspendCancellableCoroutine { cont ->
  val listener = object : ViewTreeObserver.OnPreDrawListener {
    override fun onPreDraw(): Boolean {
      cont.resume(Unit)
      viewTreeObserver.removeOnPreDrawListener(this)
      return proceedDrawingPass()
    }
  }
  cont.invokeOnCancellation {
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
 * Create an channel of scroll-change events for view.
 */
@RequiresApi(23)
suspend fun View.awaitScrollChangeEvent(): ViewScrollChangeEvent = suspendCancellableCoroutine { cont ->
  val listener = View.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
    cont.resume(ViewScrollChangeEvent(v, scrollX, scrollY, oldScrollX, oldScrollY))
    setOnScrollChangeListener(null)
  }
  cont.invokeOnCancellation {
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
 * Suspend a systemUiVisibilityChange event on view.
 */
suspend fun View.awaitSystemUiVisibilityChange(): Int = suspendCancellableCoroutine { cont ->
  val listener = View.OnSystemUiVisibilityChangeListener {
    cont.resume(it)
    setOnSystemUiVisibilityChangeListener(null)
  }
  cont.invokeOnCancellation {
    setOnSystemUiVisibilityChangeListener(null)
  }
  setOnSystemUiVisibilityChangeListener(listener)
}

/**
 * Create an channel of touch events for view.
 */
@CheckResult
fun View.touches(capacity: Int = 0): ReceiveChannel<MotionEvent> = touches(capacity) { true }

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
 * Suspend a touch event for view.
 */
suspend fun View.awaitTouch(): MotionEvent = awaitTouch { true }

/**
 * Suspend a touch event for view.
 */
suspend fun View.awaitTouch(handled: Predicate<in MotionEvent>): MotionEvent = suspendCancellableCoroutine { cont ->
  val listener = View.OnTouchListener { _, motionEvent ->
    if (handled(motionEvent)) {
      cont.resume(motionEvent)
      setOnTouchListener(null)
      true
    } else {
      false
    }
  }
  cont.invokeOnCancellation {
    setOnTouchListener(null)
  }
  setOnTouchListener(listener)
}

/**
 * Create an channel of key events for view.
 */
@CheckResult
fun View.keys(capacity: Int = 0): ReceiveChannel<KeyEvent> = keys(capacity) { true }

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

/**
 * Suspend a key event for view.
 */
suspend fun View.awaitKey(): KeyEvent = awaitKey { true }

/**
 * Suspend a key event for view.
 */
suspend fun View.awaitKey(handled: Predicate<in KeyEvent>): KeyEvent = suspendCancellableCoroutine { cont ->
  val listener = View.OnKeyListener { _, _, event ->
    if (handled(event)) {
      cont.resume(event)
      setOnKeyListener(null)
      true
    } else {
      false
    }
  }
  cont.invokeOnCancellation {
    setOnKeyListener(null)
  }
  setOnKeyListener(listener)
}
