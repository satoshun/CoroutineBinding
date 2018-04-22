@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.RendezvousChannel

private val mainHandler = Handler(Looper.getMainLooper())

inline fun <E> cancelableChannel(init: OnCancelableChannel<E>.() -> Unit): ReceiveChannel<E> {
  val channel = OnCancelableChannel<E>()
  channel.init()
  return channel
}

class OnCancelableChannel<E> : RendezvousChannel<E>() {
  lateinit var onAfterClosed: () -> Unit

  override fun afterClose(cause: Throwable?) {
    mainHandler.post { onAfterClosed() }
    super.afterClose(cause)
  }
}

val <E> OnCancelableChannel<E>.canSend get(): Boolean = !isClosedForSend
fun <E> OnCancelableChannel<E>.safeOffer(value: E): Boolean {
  return if (canSend) {
    offer(value)
    true
  } else {
    false
  }
}
