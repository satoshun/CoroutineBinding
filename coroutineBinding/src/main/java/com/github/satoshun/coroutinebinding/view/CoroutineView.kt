@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.view

import android.support.annotation.CheckResult
import android.support.annotation.RequiresApi
import android.view.DragEvent
import android.view.View
import android.view.ViewTreeObserver
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

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
inline fun View.drags(): ReceiveChannel<DragEvent> = cancelableChannel {
  val listener = View.OnDragListener { _, dragEvent ->
    safeOffer(dragEvent)
  }
  onAfterClosed = {
    setOnDragListener(null)
  }
  setOnDragListener(listener)
}

//
//@CheckResult
//inline fun View.drags(handled: Predicate<in DragEvent>): Deferred<DragEvent> = TODO()

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

//
//@CheckResult
//inline fun View.hovers(): Deferred<MotionEvent> = TODO()
//
//@CheckResult
//inline fun View.hovers(handled: Predicate<in MotionEvent>): Deferred<MotionEvent> = TODO()
//
//@CheckResult
//inline fun View.layoutChanges(): Deferred<Unit> = RxView.layoutChanges(this).map(VoidToUnit)
//
//@CheckResult
//inline fun View.layoutChangeEvents(): Deferred<ViewLayoutChangeEvent> = TODO()
//
//@CheckResult
//inline fun View.longClicks(): Deferred<Unit> = TODO()
//
//@CheckResult
//inline fun View.longClicks(handled: Callable<Boolean>): Deferred<Unit> = TODO()
//
//@CheckResult
//inline fun View.preDraws(proceedDrawingPass: Callable<Boolean>): Deferred<Unit> = TODO()
//
//@RequiresApi(23)
//@CheckResult
//inline fun View.scrollChangeEvents(): Deferred<ViewScrollChangeEvent> = TODO()
//
//@CheckResult
//inline fun View.systemUiVisibilityChanges(): Deferred<Int> = TODO()
//
//@CheckResult
//inline fun View.touches(): Deferred<MotionEvent> = TODO()
//
//@CheckResult
//inline fun View.touches(handled: Predicate<in MotionEvent>): Deferred<MotionEvent> = TODO()
//
//@CheckResult
//inline fun View.keys(): Deferred<KeyEvent> = TODO()
//
//@CheckResult
//inline fun View.keys(handled: Predicate<in KeyEvent>): Deferred<KeyEvent> = TODO()
//
//@CheckResult
//inline fun View.visibility(): Consumer<in Boolean> = TODO()
//
//@CheckResult
//inline fun View.visibility(visibilityWhenFalse: Int): Consumer<in Boolean> = TODO()
