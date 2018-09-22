package com.github.satoshun.coroutinebinding.widget

import android.database.DataSetObserver
import android.widget.Adapter
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel of data change events for Adapter.
 */
fun <T : Adapter> T.dataChanges(capacity: Int = 0): ReceiveChannel<T> = cancelableChannel(capacity) {
  val listener = object : DataSetObserver() {
    override fun onChanged() {
      safeOffer(this@dataChanges)
    }
  }
  invokeOnCloseOnMain {
    unregisterDataSetObserver(listener)
  }
  registerDataSetObserver(listener)
}

/**
 * Suspend a data change event for Adapter.
 */
suspend fun <T : Adapter> T.awaitDataChange(): T = suspendCancellableCoroutine { cont ->
  val listener = object : DataSetObserver() {
    override fun onChanged() {
      cont.resume(this@awaitDataChange)
      unregisterDataSetObserver(this)
    }
  }
  cont.invokeOnCancellation {
    unregisterDataSetObserver(listener)
  }
  registerDataSetObserver(listener)
}
