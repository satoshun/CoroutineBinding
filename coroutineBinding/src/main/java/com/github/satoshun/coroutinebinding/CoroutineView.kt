@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding

import android.support.annotation.CheckResult
import android.view.View
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel

@CheckResult
inline fun View.attaches(): ReceiveChannel<Unit> {
  val channel = Channel<Unit>()
  val listener = object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
      // do nothing
    }

    override fun onViewAttachedToWindow(v: View) {
      if (!channel.isClosedForSend) {
        channel.offer(Unit)
      }
    }
  }
  addOnAttachStateChangeListener(listener)
  // todo removeOnAttachStateChangeListener
  return channel
}

//
//@CheckResult
//inline fun View.attachEvents(): Deferred<ViewAttachEvent> = TODO()
//
//@CheckResult
//inline fun View.detaches(): Deferred<Unit> = TODO()
//
//@CheckResult
//inline fun View.clicks(): Deferred<Unit> = TODO()
//
//@CheckResult
//inline fun View.drags(): Deferred<DragEvent> = TODO()
//
//@CheckResult
//inline fun View.drags(handled: Predicate<in DragEvent>): Deferred<DragEvent> = TODO()
//
//@RequiresApi(16)
//@CheckResult
//inline fun View.draws(): Deferred<Unit> = TODO()
//
//@CheckResult
//inline fun View.focusChanges(): InitialValueObservable<Boolean> = TODO()
//
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
