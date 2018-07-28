package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.v7.widget.RecyclerView
import com.github.satoshun.coroutinebinding.cancelableChannel2
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of data change events for RecyclerView.Adapter.
 */
fun <T : RecyclerView.Adapter<out RecyclerView.ViewHolder>> T.dataChanges(capacity: Int = 0): ReceiveChannel<T> =
    cancelableChannel2(capacity) {
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
