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

inline fun <T : Adapter> AdapterView<T>.selectionEvents(): ReceiveChannel<AdapterViewSelectionEvent> = cancelableChannel {
  val listener = object : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>) {
      safeOffer(AdapterViewNothingSelectionEvent(parent))
    }

    override fun onItemSelected(parent: AdapterView<*>, selectedView: View, position: Int, id: Long) {
      safeOffer(AdapterViewItemSelectionEvent(parent, selectedView, position, id))
    }
  }
  onAfterClosed = {
    setOnItemSelectedListener(null)
  }
  setOnItemSelectedListener(listener)
}

sealed class AdapterViewSelectionEvent {
  abstract val parent: AdapterView<*>
}

class AdapterViewNothingSelectionEvent(override val parent: AdapterView<*>) : AdapterViewSelectionEvent()

data class AdapterViewItemSelectionEvent(
    override val parent: AdapterView<*>,
    val selectedView: View,
    val position: Int,
    val id: Long
) : AdapterViewSelectionEvent()

inline fun <T : Adapter> AdapterView<T>.itemClicks(): ReceiveChannel<Int> = cancelableChannel {
  val listener = AdapterView.OnItemClickListener { _, _, position, _ ->
    safeOffer(position)
  }
  onAfterClosed = {
    setOnItemClickListener(null)
  }
  setOnItemClickListener(listener)
}
