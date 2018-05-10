@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.design.widget

import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the selected item in view.
 */
inline fun BottomNavigationView.itemSelections(capacity: Int = 0): ReceiveChannel<MenuItem> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = BottomNavigationView.OnNavigationItemSelectedListener {
    safeOffer(it)
    true
  }
  onAfterClosed {
    setOnNavigationItemSelectedListener(null)
  }
  setOnNavigationItemSelectedListener(listener)

  // emit initial item
  val size = menu.size()
  for (i in 0 until size) {
    val item = menu.getItem(i)
    if (item.isChecked) {
      safeOffer(item)
      break
    }
  }
}
