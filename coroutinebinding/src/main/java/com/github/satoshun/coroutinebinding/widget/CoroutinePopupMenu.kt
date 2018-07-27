package com.github.satoshun.coroutinebinding.widget

import android.view.MenuItem
import android.widget.PopupMenu
import com.github.satoshun.coroutinebinding.cancelableChannel2
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the clicked item in PopupMenu's menu.
 */
fun PopupMenu.itemClicks(capacity: Int = 0): ReceiveChannel<MenuItem> = cancelableChannel2(capacity) {
  val listener = PopupMenu.OnMenuItemClickListener {
    safeOffer(it)
  }
  invokeOnCloseOnMain {
    setOnMenuItemClickListener(null)
  }
  setOnMenuItemClickListener(listener)
}

/**
 * Create an channel which emits the dismiss events.
 */
fun PopupMenu.dismisses(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel2(capacity) {
  val listener = PopupMenu.OnDismissListener {
    safeOffer(Unit)
  }
  invokeOnCloseOnMain {
    setOnDismissListener(null)
  }
  setOnDismissListener(listener)
}
