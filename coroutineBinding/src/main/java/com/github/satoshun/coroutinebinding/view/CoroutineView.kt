@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.view

import android.support.annotation.CheckResult
import android.support.annotation.RequiresApi
import android.view.DragEvent
import android.view.View
import android.view.ViewTreeObserver
import com.github.satoshun.coroutinebinding.OnCancelableChannel
import com.github.satoshun.coroutinebinding.canSend
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * todo
 */
@CheckResult
inline fun View.attaches(): ReceiveChannel<Unit> {
  val channel = OnCancelableChannel<Unit>()
  val listener = object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
      // do nothing
    }

    override fun onViewAttachedToWindow(v: View) {
      if (channel.canSend) {
        channel.offer(Unit)
      }
    }
  }
  channel.onAfterClosed = {
    removeOnAttachStateChangeListener(listener)
  }
  addOnAttachStateChangeListener(listener)
  return channel
}

/**
 * todo
 */
@CheckResult
inline fun View.detaches(): ReceiveChannel<Unit> {
  val channel = OnCancelableChannel<Unit>()
  val listener = object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
      if (channel.canSend) {
        channel.offer(Unit)
      }
    }

    override fun onViewAttachedToWindow(v: View) {
      // do nothing
    }
  }
  channel.onAfterClosed = {
    removeOnAttachStateChangeListener(listener)
  }
  addOnAttachStateChangeListener(listener)
  return channel
}

/**
 * todo
 */
@CheckResult
inline fun View.clicks(): ReceiveChannel<Unit> {
  val channel = OnCancelableChannel<Unit>()
  val listener = View.OnClickListener {
    if (channel.canSend) {
      channel.offer(Unit)
    }
  }
  channel.onAfterClosed = {
    setOnClickListener(null)
  }
  setOnClickListener(listener)
  return channel
}

@CheckResult
inline fun View.drags(): ReceiveChannel<DragEvent> {
  val channel = OnCancelableChannel<DragEvent>()
  val listener = View.OnDragListener { _, dragEvent ->
    if (channel.canSend) {
      channel.offer(dragEvent)
      true
    } else {
      false
    }
  }
  channel.onAfterClosed = {
    setOnDragListener(null)
  }
  setOnDragListener(listener)
  return channel
}

//
//@CheckResult
//inline fun View.drags(handled: Predicate<in DragEvent>): Deferred<DragEvent> = TODO()

/**
 * todo
 */
@RequiresApi(16)
@CheckResult
inline fun View.draws(): ReceiveChannel<Unit> {
  val channel = OnCancelableChannel<Unit>()
  val listener = ViewTreeObserver.OnDrawListener {
    if (channel.canSend) {
      channel.offer(Unit)
    }
  }
  channel.onAfterClosed = {
    viewTreeObserver.removeOnDrawListener(listener)
  }
  viewTreeObserver.addOnDrawListener(listener)
  return channel
}

/**
 * todo
 */
@CheckResult
inline fun View.focusChanges(): ReceiveChannel<Boolean> {
  val channel = OnCancelableChannel<Boolean>()
  val listener = View.OnFocusChangeListener { _, hasFocus ->
    if (channel.canSend) channel.offer(hasFocus)
  }
  channel.onAfterClosed = {
    onFocusChangeListener = null
  }
  onFocusChangeListener = listener
  return channel
}

//@CheckResult
//inline fun View.globalLayouts(): Deferred<Unit> = TODO()
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
