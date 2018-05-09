@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.design.widget

import android.support.design.widget.AppBarLayout
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the offset change in view.
 */
inline fun AppBarLayout.offsetChanges(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = AppBarLayout.OnOffsetChangedListener { _, offset ->
    safeOffer(offset)
  }
  onAfterClosed {
    removeOnOffsetChangedListener(listener)
  }
  addOnOffsetChangedListener(listener)
}
