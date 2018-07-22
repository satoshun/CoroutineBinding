@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding

import android.os.Handler
import android.os.Looper
import android.support.annotation.RestrictTo
import android.support.annotation.RestrictTo.Scope.LIBRARY
import kotlinx.coroutines.experimental.channels.AbstractChannel
import kotlinx.coroutines.experimental.channels.ArrayChannel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.RendezvousChannel

private val mainHandler = Handler(Looper.getMainLooper())

// todo: refactoring after https://github.com/Kotlin/kotlinx.coroutines/issues/341
@RestrictTo(LIBRARY)
inline fun <E> cancelableChannel(
  capacity: Int = 0,
  init: AbstractChannel<E>.((() -> Unit) -> Unit) -> Unit
): ReceiveChannel<E> {
  if (capacity == 0) {
    val channel = OnCancelableChannel<E>()
    channel.init { channel.onAfterClosed = it }
    return channel
  }
  val channel = ArrayOnCancelableChannel<E>(capacity)
  channel.init { channel.onAfterClosed = it }
  return channel
}

class OnCancelableChannel<E> : RendezvousChannel<E>() {
  lateinit var onAfterClosed: () -> Unit

  override fun afterClose(cause: Throwable?) {
    mainHandler.post { onAfterClosed() }
    super.afterClose(cause)
  }
}

@RestrictTo(LIBRARY)
class ArrayOnCancelableChannel<E>(capacity: Int) : ArrayChannel<E>(capacity) {
  lateinit var onAfterClosed: () -> Unit

  override fun afterClose(cause: Throwable?) {
    mainHandler.post { onAfterClosed() }
    super.afterClose(cause)
  }
}

val <E> AbstractChannel<E>.canSend get(): Boolean = !isClosedForSend
fun <E> AbstractChannel<E>.safeOffer(value: E): Boolean {
  return if (canSend) {
    offer(value)
    true
  } else {
    false
  }
}
