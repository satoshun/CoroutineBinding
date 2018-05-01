@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * todo
 */
inline fun AutoCompleteTextView.itemClickEvents(): ReceiveChannel<AdapterViewItemClickEvent> = cancelableChannel {
  val listener = AdapterView.OnItemClickListener { parent, view, position, id ->
    safeOffer(AdapterViewItemClickEvent(parent, view, position, id))
  }
  it {
    onItemClickListener = null
  }
  onItemClickListener = listener
}
