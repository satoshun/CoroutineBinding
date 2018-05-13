package com.github.satoshun.coroutinebinding.widget

import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of item click events on AutoCompleteTextView
 */
fun AutoCompleteTextView.itemClickEvents(capacity: Int = 0): ReceiveChannel<AdapterViewItemClickEvent> = cancelableChannel(capacity) {
  val listener = AdapterView.OnItemClickListener { parent, view, position, id ->
    safeOffer(AdapterViewItemClickEvent(parent, view, position, id))
  }
  it {
    onItemClickListener = null
  }
  onItemClickListener = listener
}
