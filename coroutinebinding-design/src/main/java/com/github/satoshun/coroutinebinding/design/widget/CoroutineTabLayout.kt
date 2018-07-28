package com.github.satoshun.coroutinebinding.design.widget

import android.support.annotation.CheckResult
import android.support.design.widget.TabLayout
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the selected tab in view.
 */
@CheckResult
fun TabLayout.selections(capacity: Int = 0): ReceiveChannel<TabLayout.Tab> =
    cancelableChannel(capacity) {
      val listener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab) {
          safeOffer(tab)
        }
      }
      invokeOnCloseOnMain {
        removeOnTabSelectedListener(listener)
      }
      addOnTabSelectedListener(listener)

      val index = selectedTabPosition
      if (index != -1) {
        safeOffer(getTabAt(index)!!)
      }
    }

/**
 * Create an channel which emits selection, reselection, and unselection events for the tabs in view.
 */
@CheckResult
fun TabLayout.selectionEvents(capacity: Int = 0): ReceiveChannel<TabLayoutSelectionEvent> =
    cancelableChannel(capacity) {
      val listener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab) {
          safeOffer(TabLayoutSelectionReselectedEvent(this@selectionEvents, tab))
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
          safeOffer(TabLayoutSelectionUnselectedEvent(this@selectionEvents, tab))
        }

        override fun onTabSelected(tab: TabLayout.Tab) {
          safeOffer(TabLayoutSelectionSelectedEvent(this@selectionEvents, tab))
        }
      }
      invokeOnCloseOnMain {
        removeOnTabSelectedListener(listener)
      }
      addOnTabSelectedListener(listener)

      val index = selectedTabPosition
      if (index != -1) {
        safeOffer(TabLayoutSelectionSelectedEvent(this@selectionEvents, getTabAt(index)!!))
      }
    }

/**
 * A tab select event on TabLayout
 */
sealed class TabLayoutSelectionEvent {
  abstract val view: TabLayout
  abstract val tab: TabLayout.Tab
}

/**
 * A tab selected event on TabLayout
 */
data class TabLayoutSelectionSelectedEvent(
  override val view: TabLayout,
  override val tab: TabLayout.Tab
) : TabLayoutSelectionEvent()

/**
 * A tab unselected event on TabLayout
 */
data class TabLayoutSelectionUnselectedEvent(
  override val view: TabLayout,
  override val tab: TabLayout.Tab
) : TabLayoutSelectionEvent()

/**
 * A tab reselected event on TabLayout
 */
data class TabLayoutSelectionReselectedEvent(
  override val view: TabLayout,
  override val tab: TabLayout.Tab
) : TabLayoutSelectionEvent()
