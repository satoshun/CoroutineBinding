package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.annotation.CheckResult
import android.support.v7.widget.ActionMenuView
import android.view.MenuItem
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the clicked menu item in view.
 */
@CheckResult
fun ActionMenuView.itemClicks(capacity: Int = 0): ReceiveChannel<MenuItem> =
    cancelableChannel(capacity) {
      val listener = ActionMenuView.OnMenuItemClickListener {
        safeOffer(it)
        true
      }
      invokeOnCloseOnMain {
        setOnMenuItemClickListener(null)
      }
      setOnMenuItemClickListener(listener)
    }
