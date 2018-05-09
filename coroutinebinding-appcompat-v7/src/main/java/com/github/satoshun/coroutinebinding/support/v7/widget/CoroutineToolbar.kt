@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.annotation.CheckResult
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the clicked item in views menu.
 */
@CheckResult
inline fun Toolbar.itemClicks(capacity: Int = 0): ReceiveChannel<MenuItem> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = Toolbar.OnMenuItemClickListener {
    safeOffer(it)
    true
  }
  onAfterClosed {
    setOnMenuItemClickListener(null)
  }
  setOnMenuItemClickListener(listener)
}

/**
 * Create an channel which emits on view navigation click events.
 */
@CheckResult
inline fun Toolbar.navigationClicks(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = View.OnClickListener {
    safeOffer(Unit)
  }
  onAfterClosed {
    setNavigationOnClickListener(null)
  }
  setNavigationOnClickListener(listener)
}
