package com.github.satoshun.coroutinebinding.widget

import android.view.MenuItem
import android.widget.PopupMenu
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Create an channel which emits the clicked item in PopupMenu's menu.
 */
fun PopupMenu.itemClicks(capacity: Int = 0): ReceiveChannel<MenuItem> = cancelableChannel(capacity) {
  val listener = PopupMenu.OnMenuItemClickListener {
    safeOffer(it)
  }
  invokeOnCloseOnMain {
    setOnMenuItemClickListener(null)
  }
  setOnMenuItemClickListener(listener)
}

/**
 * Suspend a which emits the clicked item event in PopupMenu's menu.
 */
suspend fun PopupMenu.awaitItemClick(): MenuItem = suspendCancellableCoroutine { cont ->
  val listener = PopupMenu.OnMenuItemClickListener {
    cont.resume(it)
    setOnMenuItemClickListener(null)
    true
  }
  cont.invokeOnCancellation {
    setOnMenuItemClickListener(null)
  }
  setOnMenuItemClickListener(listener)
}

/**
 * Create an channel which emits the dismiss events.
 */
fun PopupMenu.dismisses(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = PopupMenu.OnDismissListener {
    safeOffer(Unit)
  }
  invokeOnCloseOnMain {
    setOnDismissListener(null)
  }
  setOnDismissListener(listener)
}

/**
 * Suspend a which emits the dismiss event.
 */
suspend fun PopupMenu.awaitDismiss(): Unit = suspendCancellableCoroutine { cont ->
  val listener = PopupMenu.OnDismissListener {
    cont.resume(Unit)
    setOnDismissListener(null)
  }
  cont.invokeOnCancellation {
    setOnDismissListener(null)
  }
  setOnDismissListener(listener)
}
