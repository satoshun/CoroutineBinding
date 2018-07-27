@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.cancelableChannel2
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import com.github.satoshun.coroutinebinding.view.Callable
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of item selection events on AdapterView.
 */
fun <T : Adapter> AdapterView<T>.itemSelections(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel2(capacity) {
  val listener = object : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>) {
      safeOffer(AdapterView.INVALID_POSITION)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
      safeOffer(position)
    }
  }
  invokeOnCloseOnMain {
    setOnItemSelectedListener(null)
  }
  setOnItemSelectedListener(listener)
}

/**
 * Create an channel of item selection events on AdapterView.
 */
fun <T : Adapter> AdapterView<T>.selectionEvents(capacity: Int = 0): ReceiveChannel<AdapterViewSelectionEvent> =
    cancelableChannel(capacity) {
      val listener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>) {
          safeOffer(AdapterViewNothingSelectionEvent(parent))
        }

        override fun onItemSelected(parent: AdapterView<*>, selectedView: View, position: Int, id: Long) {
          safeOffer(AdapterViewItemSelectionEvent(parent, selectedView, position, id))
        }
      }
      it {
        setOnItemSelectedListener(null)
      }
      setOnItemSelectedListener(listener)
    }

/**
 * A select event on AdapterView
 */
sealed class AdapterViewSelectionEvent {
  abstract val parent: AdapterView<*>
}

/**
 * A nothing select event on AdapterView
 */
class AdapterViewNothingSelectionEvent(override val parent: AdapterView<*>) : AdapterViewSelectionEvent()

/**
 * A select event on AdapterView
 */
data class AdapterViewItemSelectionEvent(
  override val parent: AdapterView<*>,
  val selectedView: View,
  val position: Int,
  val id: Long
) : AdapterViewSelectionEvent()

/**
 * Create an channel of item click events on AdapterView.
 */
fun <T : Adapter> AdapterView<T>.itemClicks(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel(capacity) {
  val listener = AdapterView.OnItemClickListener { _, _, position, _ ->
    safeOffer(position)
  }
  it {
    setOnItemClickListener(null)
  }
  setOnItemClickListener(listener)
}

/**
 * c
 */
fun <T : Adapter> AdapterView<T>.itemClickEvents(capacity: Int = 0): ReceiveChannel<AdapterViewItemClickEvent> =
    cancelableChannel(capacity) {
      val listener = AdapterView.OnItemClickListener { parent, view, position, id ->
        safeOffer(AdapterViewItemClickEvent(parent, view, position, id))
      }
      it {
        setOnItemClickListener(null)
      }
      setOnItemClickListener(listener)
    }

/**
 * Create an channel of the position of item long-clicks for view.
 */
inline fun <T : Adapter> AdapterView<T>.itemLongClicks(capacity: Int = 0): ReceiveChannel<Int> =
    itemLongClicks(capacity) { true }

/**
 * Create an channel of the position of item long-clicks for view.
 */
fun <T : Adapter> AdapterView<T>.itemLongClicks(capacity: Int = 0, handled: Callable): ReceiveChannel<Int> =
    cancelableChannel(capacity) {
      val listener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
        if (handled()) {
          safeOffer(position)
        } else {
          false
        }
      }
      it {
        setOnItemLongClickListener(null)
      }
      setOnItemLongClickListener(listener)
    }

/**
 * Create an channel of the item long-click events for view.
 */
inline fun <T : Adapter> AdapterView<T>.itemLongClickEvents(capacity: Int = 0): ReceiveChannel<AdapterViewItemLongClickEvent> =
    itemLongClickEvents(capacity) { true }

/**
 * Create an channel of the item long-click events for view.
 */
fun <T : Adapter> AdapterView<T>.itemLongClickEvents(
  capacity: Int = 0,
  handled: Callable
): ReceiveChannel<AdapterViewItemLongClickEvent> = cancelableChannel(capacity) {
  val listener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
    if (handled()) {
      safeOffer(AdapterViewItemLongClickEvent(parent, view, position, id))
    } else {
      false
    }
  }
  it {
    setOnItemLongClickListener(null)
  }
  setOnItemLongClickListener(listener)
}

/**
 * A long click event on AdapterView.
 */
data class AdapterViewItemLongClickEvent(
  val view: AdapterView<*>,
  val clickedView: View,
  val position: Int,
  val id: Long
)
