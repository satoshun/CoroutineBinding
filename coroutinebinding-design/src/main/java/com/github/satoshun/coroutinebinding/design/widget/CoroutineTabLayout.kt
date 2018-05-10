@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.design.widget

import android.support.design.widget.TabLayout
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channle which emits the selected tab in view.
 */
inline fun TabLayout.selections(capacity: Int = 0): ReceiveChannel<TabLayout.Tab> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = object : TabLayout.OnTabSelectedListener {
    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
      safeOffer(tab)
    }
  }
  onAfterClosed {
    removeOnTabSelectedListener(listener)
  }
  addOnTabSelectedListener(listener)

  // initialize
  val index = selectedTabPosition
  if (index != -1) {
    safeOffer(getTabAt(index)!!)
  }
}
