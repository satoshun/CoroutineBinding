package com.github.satoshun.coroutinebinding.widget

import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import com.github.satoshun.coroutinebinding.view.Callable
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Create an channel of item selection events on AdapterView.
 */
fun <T : Adapter> AdapterView<T>.itemSelections(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel(capacity) {
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
 * Suspend a item selection event on AdapterView.
 */
suspend fun <T : Adapter> AdapterView<T>.awaitItemSelection(): Int = suspendCancellableCoroutine { cont ->
  val listener = object : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>) {
      cont.resume(AdapterView.INVALID_POSITION)
      onItemSelectedListener = null
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
      cont.resume(position)
      onItemSelectedListener = null
    }
  }
  cont.invokeOnCancellation {
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
      invokeOnCloseOnMain {
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
data class AdapterViewNothingSelectionEvent(
  override val parent: AdapterView<*>
) : AdapterViewSelectionEvent()

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
 * Suspend a item selection event on AdapterView.
 */
suspend fun <T : Adapter> AdapterView<T>.awaitSelectionEvent(): AdapterViewSelectionEvent =
    suspendCancellableCoroutine { cont ->
      val listener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>) {
          cont.resume(AdapterViewNothingSelectionEvent(parent))
          onItemSelectedListener = null
        }

        override fun onItemSelected(parent: AdapterView<*>, selectedView: View, position: Int, id: Long) {
          cont.resume(AdapterViewItemSelectionEvent(parent, selectedView, position, id))
          onItemSelectedListener = null
        }
      }
      cont.invokeOnCancellation {
        setOnItemSelectedListener(null)
      }
      setOnItemSelectedListener(listener)
    }

/**
 * Create an channel of item click events on AdapterView.
 */
fun <T : Adapter> AdapterView<T>.itemClicks(capacity: Int = 0): ReceiveChannel<Int> =
    cancelableChannel(capacity) {
      val listener = AdapterView.OnItemClickListener { _, _, position, _ ->
        safeOffer(position)
      }
      invokeOnCloseOnMain {
        setOnItemClickListener(null)
      }
      setOnItemClickListener(listener)
    }

/**
 * Create an channel of item click events on AdapterView
 */
fun <T : Adapter> AdapterView<T>.itemClickEvents(capacity: Int = 0): ReceiveChannel<AdapterViewItemClickEvent> =
    cancelableChannel(capacity) {
      val listener = AdapterView.OnItemClickListener { parent, view, position, id ->
        safeOffer(AdapterViewItemClickEvent(parent, view, position, id))
      }
      invokeOnCloseOnMain {
        setOnItemClickListener(null)
      }
      setOnItemClickListener(listener)
    }

/**
 * Suspend a item click event on AdapterView
 */
suspend fun <T : Adapter> AdapterView<T>.awaitItemClickEvent(): AdapterViewItemClickEvent =
    suspendCancellableCoroutine { cont ->
      val listener = AdapterView.OnItemClickListener { parent, view, position, id ->
        cont.resume(AdapterViewItemClickEvent(parent, view, position, id))
        setOnItemClickListener(null)
      }
      cont.invokeOnCancellation {
        setOnItemClickListener(null)
      }
      setOnItemClickListener(listener)
    }

/**
 * Create an channel of the position of item long-clicks for view.
 */
fun <T : Adapter> AdapterView<T>.itemLongClicks(capacity: Int = 0): ReceiveChannel<Int> =
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
      invokeOnCloseOnMain {
        setOnItemLongClickListener(null)
      }
      setOnItemLongClickListener(listener)
    }

/**
 * Suspend a position of item long-click for view.
 */
suspend fun <T : Adapter> AdapterView<T>.awaitItemLongClick(): Int = awaitItemLongClick { true }

/**
 * Suspend a position of item long-click for view.
 */
suspend fun <T : Adapter> AdapterView<T>.awaitItemLongClick(handled: Callable): Int =
    suspendCancellableCoroutine { cont ->
      val listener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
        if (handled()) {
          cont.resume(position)
          onItemLongClickListener = null
          true
        } else {
          false
        }
      }
      cont.invokeOnCancellation {
        setOnItemLongClickListener(null)
      }
      setOnItemLongClickListener(listener)
    }

/**
 * Create an channel of the item long-click events for view.
 */
fun <T : Adapter> AdapterView<T>.itemLongClickEvents(capacity: Int = 0): ReceiveChannel<AdapterViewItemLongClickEvent> =
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
  invokeOnCloseOnMain {
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

/**
 * Suspend a item long-click event for view.
 */
suspend fun <T : Adapter> AdapterView<T>.awaitItemLongClickEvent(): AdapterViewItemLongClickEvent =
    awaitItemLongClickEvent { true }

/**
 * Suspend a item long-click event for view.
 */
suspend fun <T : Adapter> AdapterView<T>.awaitItemLongClickEvent(
  handled: Callable
): AdapterViewItemLongClickEvent = suspendCancellableCoroutine { cont ->
  val listener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
    if (handled()) {
      cont.resume(AdapterViewItemLongClickEvent(parent, view, position, id))
      onItemLongClickListener = null
      true
    } else {
      false
    }
  }
  cont.invokeOnCancellation {
    setOnItemLongClickListener(null)
  }
  setOnItemLongClickListener(listener)
}
