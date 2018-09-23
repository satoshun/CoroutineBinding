package com.github.satoshun.coroutinebinding.androidx.recyclerview.widget

import androidx.recyclerview.widget.RecyclerView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel of data change events for RecyclerView.Adapter.
 */
fun <T : RecyclerView.Adapter<out RecyclerView.ViewHolder>> T.dataChanges(capacity: Int = 0): ReceiveChannel<T> =
    cancelableChannel(capacity) {
      val listener = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
          safeOffer(this@dataChanges)
        }
      }
      invokeOnCloseOnMain {
        unregisterAdapterDataObserver(listener)
      }
      registerAdapterDataObserver(listener)
    }

/**
 * Suspend a of data change event for RecyclerView.Adapter.
 */
suspend fun <T : RecyclerView.Adapter<out RecyclerView.ViewHolder>> T.awaitDataChange(): T =
    suspendCancellableCoroutine { cont ->
      val listener = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
          cont.resume(this@awaitDataChange)
          unregisterAdapterDataObserver(this)
        }
      }
      cont.invokeOnCancellation {
        unregisterAdapterDataObserver(listener)
      }
      registerAdapterDataObserver(listener)
    }
