@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding

import android.os.Handler
import android.os.Looper
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel

private val mainHandler = Handler(Looper.getMainLooper())

@RestrictTo(LIBRARY)
inline fun <E> cancelableChannel(
  capacity: Int = 0,
  block: Channel<E>.() -> Unit
): ReceiveChannel<E> {
  val channel = Channel<E>(capacity)
  channel.block()
  return channel
}

private val <E> Channel<E>.canSend get(): Boolean = !isClosedForSend

@RestrictTo(LIBRARY)
fun <E> Channel<E>.invokeOnCloseOnMain(handler: (cause: Throwable?) -> Unit) {
  invokeOnClose { throwable ->
    mainHandler.post { handler(throwable) }
  }
}

@RestrictTo(LIBRARY)
fun <E> Channel<E>.safeOffer(value: E): Boolean {
  return if (canSend) {
    offer(value)
    true
  } else {
    false
  }
}
