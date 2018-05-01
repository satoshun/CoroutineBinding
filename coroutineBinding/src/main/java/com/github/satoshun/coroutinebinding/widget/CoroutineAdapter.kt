package com.github.satoshun.coroutinebinding.widget

import android.database.DataSetObserver
import android.widget.Adapter
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

fun <T : Adapter> T.dataChanges(): ReceiveChannel<T> = cancelableChannel { onAfterClosed ->
  val listener = object : DataSetObserver() {
    override fun onChanged() {
      safeOffer(this@dataChanges)
    }
  }
  onAfterClosed {
    registerDataSetObserver(null)
  }
  registerDataSetObserver(listener)
}
