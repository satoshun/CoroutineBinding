package com.github.satoshun.coroutinebinding

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.experimental.channels.RendezvousChannel

private val mainHandler = Handler(Looper.getMainLooper())

class OnCancelableChannel<E> : RendezvousChannel<E>() {
  lateinit var onAfterClosed: () -> Unit

  override fun afterClose(cause: Throwable?) {
    mainHandler.post { onAfterClosed() }
    super.afterClose(cause)
  }
}

val <E> OnCancelableChannel<E>.canSend get(): Boolean = !isClosedForSend
