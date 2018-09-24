package com.github.satoshun.coroutinebinding.androidx.appcompat.widget

import android.view.MenuItem
import androidx.annotation.CheckResult
import androidx.appcompat.widget.PopupMenu
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

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
 * Suspend a which emits the clicked item in views menu.
 */
@CheckResult
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

/**
 * Suspend a which emits on view dismiss event.
 */
@CheckResult
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
