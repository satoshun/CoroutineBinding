package com.github.satoshun.coroutinebinding

import kotlinx.coroutines.experimental.channels.RendezvousChannel

class OnCancelableChannel<E> : RendezvousChannel<E>() {
  lateinit var onAfterClosed: () -> Unit

  override fun afterClose(cause: Throwable?) {
    onAfterClosed()
    super.afterClose(cause)
  }
}
