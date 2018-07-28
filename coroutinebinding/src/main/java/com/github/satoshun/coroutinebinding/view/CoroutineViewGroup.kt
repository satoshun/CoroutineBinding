package com.github.satoshun.coroutinebinding.view

import android.view.View
import android.view.ViewGroup
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of hierarchy change events for ViewGroup.
 */
fun ViewGroup.changeEvents(capacity: Int = 0): ReceiveChannel<ViewGroupHierarchyChangeEvent> =
    cancelableChannel(capacity) {
      val listener = object : ViewGroup.OnHierarchyChangeListener {
        override fun onChildViewRemoved(parent: View, child: View) {
          safeOffer(ViewGroupHierarchyChildViewRemoveEvent(this@changeEvents, child))
        }

        override fun onChildViewAdded(parent: View, child: View) {
          safeOffer(ViewGroupHierarchyChildViewAddEvent(this@changeEvents, child))
        }
      }
      invokeOnCloseOnMain {
        setOnHierarchyChangeListener(null)
      }
      setOnHierarchyChangeListener(listener)
    }

/**
 * A child view event on a ViewGroup.
 */
sealed class ViewGroupHierarchyChangeEvent {
  abstract val view: ViewGroup
  abstract val child: View
}

/**
 * A child view add event on a ViewGroup.
 */
class ViewGroupHierarchyChildViewAddEvent(
  override val view: ViewGroup,
  override val child: View
) : ViewGroupHierarchyChangeEvent()

/**
 * A child view remove event on a ViewGroup.
 */
class ViewGroupHierarchyChildViewRemoveEvent(
  override val view: ViewGroup,
  override val child: View
) : ViewGroupHierarchyChangeEvent()
