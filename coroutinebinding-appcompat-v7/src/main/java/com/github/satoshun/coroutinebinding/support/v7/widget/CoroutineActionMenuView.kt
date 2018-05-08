@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.support.v7.widget

import android.view.MenuItem
import android.support.v7.widget.ActionMenuView
import com.github.satoshun.coroutinebinding.cancelableChannel
import kotlinx.coroutines.experimental.channels.ReceiveChannel

inline fun ActionMenuView.itemClicks(capacity: Int = 0): ReceiveChannel<MenuItem> = cancelableChannel {
}
