@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import com.github.satoshun.coroutinebinding.view.Callable
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

inline fun <T : Adapter> AdapterView<T>.itemClickEvents(): ReceiveChannel<AdapterViewItemClickEvent> = cancelableChannel {
  val listener = AdapterView.OnItemClickListener { parent, view, position, id ->
    safeOffer(AdapterViewItemClickEvent(parent, view, position, id))
  }
  onAfterClosed = {
    setOnItemClickListener(null)
  }
  setOnItemClickListener(listener)
}

inline fun <T : Adapter> AdapterView<T>.itemLongClicks(): ReceiveChannel<Int> = itemLongClicks { true }

inline fun <T : Adapter> AdapterView<T>.itemLongClicks(crossinline handled: Callable): ReceiveChannel<Int> = cancelableChannel {
  val listener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
    if (handled()) {
      safeOffer(position)
    } else {
      false
    }
  }
  onAfterClosed = {
    setOnItemLongClickListener(null)
  }
  setOnItemLongClickListener(listener)
}

inline fun <T : Adapter> AdapterView<T>.itemLongClickEvents(): ReceiveChannel<AdapterViewItemLongClickEvent> = itemLongClickEvents { true }

inline fun <T : Adapter> AdapterView<T>.itemLongClickEvents(crossinline handled: Callable): ReceiveChannel<AdapterViewItemLongClickEvent> = cancelableChannel {
  val listener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
    if (handled()) {
      safeOffer(AdapterViewItemLongClickEvent(parent, view, position, id))
    } else {
      false
    }
  }
  onAfterClosed = {
    setOnItemLongClickListener(null)
  }
  setOnItemLongClickListener(listener)
}

data class AdapterViewItemLongClickEvent(
    val view: AdapterView<*>,
    val clickedView: View,
    val position: Int,
    val id: Long
)
