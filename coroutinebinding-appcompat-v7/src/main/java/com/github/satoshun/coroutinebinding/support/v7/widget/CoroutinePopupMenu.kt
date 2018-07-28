package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.annotation.CheckResult
import android.support.v7.widget.PopupMenu
import android.view.MenuItem
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the clicked item in views menu.
 */
@CheckResult
fun PopupMenu.itemClicks(capacity: Int = 0): ReceiveChannel<MenuItem> = cancelableChannel(capacity) {
  val listener = PopupMenu.OnMenuItemClickListener {
    safeOffer(it)
    true
  }
  invokeOnCloseOnMain {
    setOnMenuItemClickListener(null)
  }
  setOnMenuItemClickListener(listener)
}

/**
 * Create an channel which emits on view dismiss events.
 */
@CheckResult
fun PopupMenu.dismisses(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = PopupMenu.OnDismissListener {
    safeOffer(Unit)
  }
  invokeOnCloseOnMain {
    setOnDismissListener(null)
  }
  setOnDismissListener(listener)
}
