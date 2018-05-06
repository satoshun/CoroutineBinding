@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.v7.widget.RecyclerView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of data change events for [RecyclerView.Adapter].
 */
inline fun <T : RecyclerView.Adapter<out RecyclerView.ViewHolder>> T.dataChanges(capacity: Int = 0): ReceiveChannel<T> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = object : RecyclerView.AdapterDataObserver() {
    override fun onChanged() {
      safeOffer(this@dataChanges)
    }
  }
  onAfterClosed {
    unregisterAdapterDataObserver(listener)
  }
  registerAdapterDataObserver(listener)
}
