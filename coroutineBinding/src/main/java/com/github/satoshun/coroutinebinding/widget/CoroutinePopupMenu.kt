@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.view.MenuItem
import android.widget.PopupMenu
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * todo
 */
inline fun PopupMenu.itemClicks(): ReceiveChannel<MenuItem> = cancelableChannel {
  val listener = PopupMenu.OnMenuItemClickListener {
    safeOffer(it)
  }
  onAfterClosed = {
    setOnMenuItemClickListener(null)
  }
  setOnMenuItemClickListener(listener)
}

/**
 * todo
 */
inline fun PopupMenu.dismisses(): ReceiveChannel<Unit> = cancelableChannel {
  val listener = PopupMenu.OnDismissListener {
    safeOffer(Unit)
  }
  onAfterClosed = {
    setOnDismissListener(null)
  }
  setOnDismissListener(listener)
}
