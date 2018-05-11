@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.view

import android.view.View
import android.view.ViewGroup
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of hierarchy change events for [ViewGroup].
 */
inline fun ViewGroup.changeEvents(capacity: Int = 0): ReceiveChannel<ViewGroupHierarchyChangeEvent> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = object : ViewGroup.OnHierarchyChangeListener {
    override fun onChildViewRemoved(parent: View, child: View) {
      safeOffer(ViewGroupHierarchyChildViewRemoveEvent(this@changeEvents, child))
    }

    override fun onChildViewAdded(parent: View, child: View) {
      safeOffer(ViewGroupHierarchyChildViewAddEvent(this@changeEvents, child))
    }
  }
  onAfterClosed {
    setOnHierarchyChangeListener(null)
  }
  setOnHierarchyChangeListener(listener)
}

sealed class ViewGroupHierarchyChangeEvent {
  abstract val view: ViewGroup
  abstract val child: View
}

class ViewGroupHierarchyChildViewAddEvent(
    override val view: ViewGroup,
    override val child: View
) : ViewGroupHierarchyChangeEvent()

class ViewGroupHierarchyChildViewRemoveEvent(
    override val view: ViewGroup,
    override val child: View
) : ViewGroupHierarchyChangeEvent()
