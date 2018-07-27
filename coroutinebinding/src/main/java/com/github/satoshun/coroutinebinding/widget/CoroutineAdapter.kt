package com.github.satoshun.coroutinebinding.widget

import android.database.DataSetObserver
import android.widget.Adapter
import com.github.satoshun.coroutinebinding.cancelableChannel2
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of data change events for Adapter.
 */
fun <T : Adapter> T.dataChanges(capacity: Int = 0): ReceiveChannel<T> = cancelableChannel2(capacity) {
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
