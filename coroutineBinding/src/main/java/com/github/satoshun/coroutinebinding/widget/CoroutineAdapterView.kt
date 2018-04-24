@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

inline fun <T : Adapter> AdapterView<T>.itemSelections(): ReceiveChannel<Int> = cancelableChannel {
  val listener = object : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>) {
      safeOffer(AdapterView.INVALID_POSITION)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
      safeOffer(position)
    }
  }
  onAfterClosed = {
    setOnItemSelectedListener(null)
  }
  setOnItemSelectedListener(listener)
}
