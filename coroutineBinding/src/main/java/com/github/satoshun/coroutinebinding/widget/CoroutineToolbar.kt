@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.support.annotation.RequiresApi
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * todo
 */
@RequiresApi(21)
inline fun Toolbar.itemClicks(): ReceiveChannel<MenuItem> = cancelableChannel {
  val listener = Toolbar.OnMenuItemClickListener {
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
@RequiresApi(21)
inline fun Toolbar.navigationClicks(): ReceiveChannel<Unit> = cancelableChannel {
  val listener = View.OnClickListener {
    safeOffer(Unit)
  }
  onAfterClosed = {
    setOnClickListener(null)
  }
  setOnClickListener(listener)
}
