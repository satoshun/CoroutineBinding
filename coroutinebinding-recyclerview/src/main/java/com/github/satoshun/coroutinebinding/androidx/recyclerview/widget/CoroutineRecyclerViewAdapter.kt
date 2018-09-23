package com.github.satoshun.coroutinebinding.androidx.recyclerview.widget

import androidx.recyclerview.widget.RecyclerView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

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
